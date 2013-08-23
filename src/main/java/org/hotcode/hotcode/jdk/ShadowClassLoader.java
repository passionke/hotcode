package org.hotcode.hotcode.jdk;

import org.hotcode.hotcode.adapter.FieldAccessTransformAdapter;
import org.hotcode.hotcode.adapter.shadow.ShadowClassAdapter;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.util.ClassDumper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

/**
 * Shadow Class Loader
 * 
 * @author zhuyong 2013-7-5 15:42:13
 */
public class ShadowClassLoader extends ClassLoader {

    public ShadowClassLoader(ClassLoader parent){
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Long index = CRMManager.getIndex(getParent());
        ClassReloaderManager classReloadManager = CRMManager.getClassReloaderManager(index);

        String originClassName = name.substring(0, name.indexOf(HotCodeConstant.HOTCODE_SHADOW_CLASS_POSTFIX));
        ClassReloader classReloader = classReloadManager.getClassReloader(classReloadManager.getIndex(originClassName));

        Class<?> clazz = null;

        if (classReloader.getVersionedClassFile() != null) {
            byte[] classFile = classReloader.getVersionedClassFile().getClassFile();
            ClassReader cr = new ClassReader(classFile);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ShadowClassAdapter(cw, name);
            cv = new ClassVisitor(Opcodes.ASM4, cv) {

                @Override
                public MethodVisitor visitMethod(final int access, String name, final String desc,
                                                 final String signature, String[] exceptions) {
                    return new FieldAccessTransformAdapter(
                                                           access,
                                                           new Method(name, desc),
                                                           super.visitMethod(access, name, desc, signature, exceptions),
                                                           CRMManager.getIndex(getParent()), 0L);
                }
            };
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            byte[] classRedefined = cw.toByteArray();

            ClassDumper.dump(name, classRedefined);
            clazz = super.defineClass(name, classRedefined, 0, classRedefined.length);
        }

        return clazz;
    }
}
