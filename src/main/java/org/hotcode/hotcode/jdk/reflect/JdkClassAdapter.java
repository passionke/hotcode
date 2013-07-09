package org.hotcode.hotcode.jdk.reflect;

import org.hotcode.hotcode.jdk.reflect.modifier.GetDeclaredFieldsModifier;
import org.hotcode.hotcode.jdk.reflect.modifier.PrivateGetDeclaredFieldsModifier;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Transform the {@link Class}.
 * 
 * @author zhuyong 2013-7-3 11:13:38
 */
public class JdkClassAdapter extends ClassVisitor {

    public JdkClassAdapter(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("privateGetDeclaredFields")) {
            return new PrivateGetDeclaredFieldsModifier(mv, access, name, desc);
        }

        if (name.equals("getDeclaredFields") || name.equals("getFields()")) {
            return new GetDeclaredFieldsModifier(mv, access, name, desc);
        }

        return mv;
    }
}
