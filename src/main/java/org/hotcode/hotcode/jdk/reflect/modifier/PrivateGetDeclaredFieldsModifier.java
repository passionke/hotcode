package org.hotcode.hotcode.jdk.reflect.modifier;

import org.hotcode.hotcode.jdk.reflect.JdkReflectHelper;
import org.hotcode.hotcode.reloader.CRMManager;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * Support privateGetDeclaredFields for class <link>Class</link>
 * 
 * @author zhuyong 2013-7-9 17:04
 */
public class PrivateGetDeclaredFieldsModifier extends GeneratorAdapter {

    public PrivateGetDeclaredFieldsModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {

        if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/lang/Class") && name.equals("getDeclaredFields0")
            && desc.equals("(Z)[Ljava/lang/reflect/Field;")) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(CRMManager.class), "hasShadowClass",
                               "(Ljava/lang/Class;)Z");
            Label old = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, old);

            mv.visitInsn(Opcodes.POP2);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ILOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(JdkReflectHelper.class),
                               "privateGetDeclaredFields0", "(Ljava/lang/Class;Z)[Ljava/lang/reflect/Field;");
            Label end = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, end);
            mv.visitLabel(old);
            super.visitMethodInsn(opcode, owner, name, desc);
            mv.visitLabel(end);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
