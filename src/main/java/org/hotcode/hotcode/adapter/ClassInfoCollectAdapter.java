package org.hotcode.hotcode.adapter;

import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Collect the info of a class
 * 
 * @author khotyn 13-6-26 PM9:19
 */
public class ClassInfoCollectAdapter extends ClassVisitor {

    private HotCodeClass hotCodeClass;

    public ClassInfoCollectAdapter(ClassVisitor cv, HotCodeClass hotCodeClass){
        super(Opcodes.ASM4, cv);
        this.hotCodeClass = hotCodeClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        hotCodeClass.setClassName(name.replace('/', '.'));
        hotCodeClass.setAccess(access);
        hotCodeClass.setSuperClassName(superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        int newAccess = access;

        if ((access & Opcodes.ACC_FINAL) != 0) {
            newAccess = newAccess - Opcodes.ACC_FINAL;
        }

        hotCodeClass.addField(new HotCodeField(newAccess, name, desc));
        return super.visitField(newAccess, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        HotCodeMethod method = new HotCodeMethod(access, name, desc, signature, exceptions);

        if ("<init>".equals(name)) {
            hotCodeClass.addConstructor(method);
        } else if (!"<clinit>".equals(name)) {
            hotCodeClass.addMethod(method);
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
