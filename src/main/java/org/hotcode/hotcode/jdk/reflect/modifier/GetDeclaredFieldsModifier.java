package org.hotcode.hotcode.jdk.reflect.modifier;

import org.hotcode.hotcode.jdk.reflect.JdkReflectHelper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * Support getDeclaredFields for class: Class
 * 
 * @author zhuyong 2013-7-9 17:04
 */
public class GetDeclaredFieldsModifier extends GeneratorAdapter {

    public GetDeclaredFieldsModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitInsn(int opcode) {
        // Filter HotCode Added Fields.
        if (opcode == Opcodes.ARETURN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(JdkReflectHelper.class),
                               "filterHotCodeFields", "([Ljava/lang/reflect/Field;)[Ljava/lang/reflect/Field;");
        }
        super.visitInsn(opcode);
    }
}
