package org.hotcode.hotcode;

import java.io.File;
import java.net.URL;

import org.hotcode.hotcode.adapter.*;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.resource.FileSystemVersionedClassFile;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.util.ClassDumper;
import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

/**
 * @author khotyn 13-6-26 PM2:17
 */
public class ClassTransformer {

    private static final String[] SKIP_PKGS = { "jdk", "javax", "sun", "com.apple.jdk" };

    /**
     * Transform a class first time it is loaded to the JVM.
     * 
     * @param className
     * @param classLoader
     * @param classfileBuffer
     * @return
     */
    public static byte[] transformNewLoadClass(String className, ClassLoader classLoader, byte[] classfileBuffer) {
        for (String SKIP_PKG : SKIP_PKGS) {
            if (className.startsWith(SKIP_PKG) || className.indexOf(HotCodeConstant.HOTCODE_SHADOW_CLASS_POSTFIX) != -1) {
                return classfileBuffer;
            }
        }

        if (className.contains("$$$")) {
            return classfileBuffer;
        }

        Long classReloaderManagerIndex = CRMManager.getIndex(classLoader);

        if (classReloaderManagerIndex == null) {
            classReloaderManagerIndex = CRMManager.putClassReloaderManager(classLoader,
                                                                           new ClassReloaderManager(classLoader));
        }

        ClassReloaderManager classReloaderManager = CRMManager.getClassReloaderManager(classReloaderManagerIndex);

        Long classReloaderIndex = classReloaderManager.getIndex(className.replace('.', '/'));

        URL classFileURL = classLoader.getResource(className.replace('.', '/') + ".class");
        File classFile = new File(classFileURL.getFile());

        if (!classFile.exists()) {
            return classfileBuffer;
        }

        FileSystemVersionedClassFile fileSystemVersionedClassFile = new FileSystemVersionedClassFile(classFile);

        HotCodeClass hotCodeClass = new HotCodeClass();

        if (classReloaderIndex == null) {
            classReloaderIndex = classReloaderManager.getNextAvailableIndex();
            classReloaderManager.putClassReloader(classReloaderIndex, className.replace('.', '/'),
                                                  new ClassReloader(classReloaderManagerIndex, classReloaderIndex,
                                                                    fileSystemVersionedClassFile, hotCodeClass,
                                                                    classLoader));
        }
        ClassReloader classReloader = CRMManager.getClassReloader(classLoader, className.replace('.', '/'));
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClinitClassAdapter(cw, classReloaderManagerIndex, classReloaderIndex);
        cv = new ConstructorTransformAdapter(cv, classReloader);
        cv = new AddFieldsHolderAdapter(cv);
        cv = new AddClassReloaderAdapter(cv);
        cv = new AddMethodRouterAdapter(cv, classReloader);
        cv = new BeforeMethodCheckAdapter(cv, classReloader);
        cv = new ClassInfoCollectAdapter(cv, hotCodeClass);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        byte[] classRedefined = cw.toByteArray();
        ClassDumper.dump(className.replace('.', '/'), classRedefined);
        return classRedefined;
    }

    /**
     * Transform the byte code of a class when it is reloading.
     * 
     * @param classReloaderManagerIndex
     * @param classReloaderIndex
     * @param classFile
     * @return
     */
    public static byte[] transformReloadClass(final Long classReloaderManagerIndex, final Long classReloaderIndex,
                                              byte[] classFile) {
        ClassReloader classReloader = CRMManager.getClassReloaderManager(classReloaderManagerIndex).getClassReloader(classReloaderIndex);
        HotCodeClass reloadedClass = HotCodeUtil.collectClassInfo(classFile);
        classReloader.setReloadedClass(reloadedClass);

        ClassReader cr = new ClassReader(classFile);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new AddFieldsHolderAdapter(cw);
        cv = new AddClassReloaderAdapter(cv);
        cv = new AddMethodRouterAdapter(cv, classReloader);
        cv = new ClassVisitor(Opcodes.ASM4, cv) {

            @Override
            public MethodVisitor visitMethod(final int access, String name, final String desc, final String signature,
                                             String[] exceptions) {
                return new MethodBodyTransformAdapter(access, new Method(name, desc), super.visitMethod(access, name,
                                                                                                        desc,
                                                                                                        signature,
                                                                                                        exceptions),
                                                      classReloaderManagerIndex, classReloaderIndex);
            }
        };
        cv = new FieldTransformAdapter(cv, classReloaderManagerIndex, classReloaderIndex);
        cv = new ConstructorTransformAdapter(cv, classReloader);
        cv = new ClinitClassAdapter(cv, classReloaderManagerIndex, classReloaderIndex);
        cv = new BeforeMethodCheckAdapter(cv, classReloader);
        cv = new MethodTransformAdapter(cv, classReloader);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}
