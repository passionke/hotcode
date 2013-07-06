package org.hotcode.hotcode;

import java.io.File;
import java.net.URL;

import org.hotcode.hotcode.adapter.*;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.resource.FileSystemVersionedClassFile;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.util.ClassDumper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Transform new load classes.
 * 
 * @author khotyn 13-6-28 PM9:30
 */
public class NewLoadClassTransformer {

    private static final String[] SKIP_PKGS = { "jdk", "javax", "sun", "com.apple.jdk" };

    public static byte[] transformNewLoadClass(String className, ClassLoader classLoader, byte[] classfileBuffer) {
        for (String SKIP_PKG : SKIP_PKGS) {
            if (className.startsWith(SKIP_PKG)) {
                return classfileBuffer;
            }
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

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new AddFieldsHolderAdapter(cw);
        cv = new AddClassReloaderAdapter(cv);
        cv = new ClinitClassAdapter(cv, classReloaderManagerIndex, classReloaderIndex);
        cv = new BeforeMethodCheckAdapter(cv);
        cv = new ClassInfoCollectAdapter(cv, hotCodeClass);
        cr.accept(cv, 0);
        byte[] classRedefined = cw.toByteArray();
        ClassDumper.dump(className.replace('.', '/'), classRedefined);
        return classRedefined;
    }
}
