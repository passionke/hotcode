package org.hotcode.hotcode.jdk.reflect.modifier;

import org.hotcode.hotcode.reloader.CRMManager;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Field declaredAnnotations() Support
 * 
 * @author zhuyong 2013-7-16
 */
public class DeclaredAnnotationsModifier extends GeneratorAdapter {

    public DeclaredAnnotationsModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/reflect/Field")
            && name.equals("getDeclaringClass") && desc.equals("()Ljava/lang/Class;")) {
            super.visitMethodInsn(opcode, owner, name, desc);
            dup();
            invokeStatic(Type.getType(CRMManager.class), new Method("getShadowClass",
                                                                    "(Ljava/lang/Class;)Ljava/lang/Class;"));
            dup();
            Label old = newLabel();
            ifNull(old);
            swap();

            mark(old);
            pop();
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
