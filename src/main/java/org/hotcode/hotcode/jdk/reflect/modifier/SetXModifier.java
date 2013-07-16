package org.hotcode.hotcode.jdk.reflect.modifier;

import org.hotcode.hotcode.jdk.reflect.JdkReflectHelper;
import org.hotcode.hotcode.util.HotCodeThreadLocalUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Field setX() Support
 * 
 * @author zhuyong 2013-7-16 12:17
 */
public class SetXModifier extends GeneratorAdapter {

    private String desc;

    public SetXModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
        this.desc = desc;
    }

    @Override
    public void visitCode() {
        loadArg(0);
        invokeStatic(Type.getType(JdkReflectHelper.class),
                     new Method("isTransformFieldAccess", "(Ljava/lang/Object;)Z"));
        Label old = newLabel();
        ifZCmp(EQ, old);

        // Mark access flag.
        invokeStatic(Type.getType(HotCodeThreadLocalUtil.class), new Method("access", "()V"));

        loadArg(0);
        loadThis();
        loadArg(1);
        box(Type.getArgumentTypes(desc)[1]);

        invokeStatic(Type.getType(JdkReflectHelper.class),
                     new Method("setFieldValue", "(Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Object;)V"));

        // Unmark access flag.
        invokeStatic(Type.getType(HotCodeThreadLocalUtil.class), new Method("clearAccess", "()V"));
        mv.visitInsn(Opcodes.RETURN);

        mark(old);
        super.visitCode();
    }
}
