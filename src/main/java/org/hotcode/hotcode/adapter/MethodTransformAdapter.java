package org.hotcode.hotcode.adapter;

import java.util.Set;

import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author khotyn.huangt 13-8-4 PM1:33
 */
public class MethodTransformAdapter extends ClassVisitor {

    private ClassReloader classReloader;

    public MethodTransformAdapter(ClassVisitor cv, ClassReloader classReloader){
        super(Opcodes.ASM4, cv);
        this.classReloader = classReloader;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("<init>") || name.equals("<clinit>")) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        Set<HotCodeMethod> methods = classReloader.getOriginClass().getMethods();
        HotCodeMethod method = new HotCodeMethod(access, name, desc, signature, exceptions);

        if (!methods.contains(method)) {
            return null;
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
