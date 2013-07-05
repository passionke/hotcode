package org.hotcode.hotcode;

import org.hotcode.hotcode.adapters.AddClassReloaderAdapter;
import org.hotcode.hotcode.adapters.AddFieldsHolderAdapter;
import org.hotcode.hotcode.adapters.BeforeMethodCheckAdapter;
import org.hotcode.hotcode.adapters.ClinitClassAdapter;
import org.hotcode.hotcode.adapters.FieldTransformAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author khotyn 13-6-26 PM2:17
 */
public class ClassTransformer {

    public static byte[] transform(Long classReloaderManagerIndex, Long classReloaderIndex, byte[] classFile) {
        ClassReader cr = new ClassReader(classFile);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new AddFieldsHolderAdapter(cw);
        cv = new AddClassReloaderAdapter(cv);
        cv = new FieldTransformAdapter(cv, classReloaderManagerIndex, classReloaderIndex);
        cv = new ClinitClassAdapter(cv, classReloaderManagerIndex, classReloaderIndex);
        cv = new BeforeMethodCheckAdapter(cv);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
