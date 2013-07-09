package org.hotcode.hotcode.java.lang.reflect;

import org.hotcode.hotcode.java.lang.reflect.modifier.PrivateGetDeclaredFieldsModifier;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author zhuyong 2013-7-3 11:13:38
 */
public class JdkClassAdapter extends ClassVisitor {

    protected String className;

    public JdkClassAdapter(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("privateGetDeclaredFields")) {
            return new PrivateGetDeclaredFieldsModifier(mv, access, name, desc);
        }

        // if (name.equals("getDeclaredFields") || name.equals("getFields()")) {
        // return new GetDeclaredFieldsModifier(mv, access, name, desc, className);
        // }

        return mv;
    }
}
