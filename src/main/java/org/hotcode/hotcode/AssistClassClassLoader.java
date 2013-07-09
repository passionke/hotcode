package org.hotcode.hotcode;

import org.hotcode.hotcode.adapter.AssistClassAdapter;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.util.ClassDumper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Class loader used to load assist classes.
 * 
 * @author khotyn 13-7-6 PM10:08
 */
public class AssistClassClassLoader extends ClassLoader {

    private ClassReloader classReloader;

    public AssistClassClassLoader(ClassLoader parent, ClassReloader classReloader){
        super(parent);
        this.classReloader = classReloader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] originClassFile = classReloader.getClassFileByte();
        ClassReader cr = new ClassReader(originClassFile);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new AssistClassAdapter(cw, name);
        cr.accept(cv, 0);
        byte[] assistClassFile = cw.toByteArray();
        ClassDumper.dump(name, assistClassFile);
        return this.defineClass(name, assistClassFile, 0, assistClassFile.length);
    }
}
