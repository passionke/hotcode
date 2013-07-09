package org.hotcode.hotcode.java.lang.reflect.modifier;

import org.hotcode.hotcode.java.lang.reflect.helper.JdkFieldReflectHelper;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Field getX() Support
 * 
 * @author zhuyong 2013-7-3 11:13:38
 */
public class GetXModifier extends GeneratorAdapter {

    private String desc;

    public GetXModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
        this.desc = desc;
    }

    @Override
    public void visitCode() {
        super.visitCode();

        loadArg(0);
        loadThis();
        invokeStatic(Type.getType(JdkFieldReflectHelper.class),
                     new Method("hasShadowClass", "(Ljava/lang/Object;Ljava/lang/reflect/Field;)Z"));
        Label old = newLabel();
        ifZCmp(EQ, old);

        loadArg(0);
        loadThis();

        invokeStatic(Type.getType(JdkFieldReflectHelper.class),
                     new Method("getHotswapFieldHolderValue",
                                "(Ljava/lang/Object;Ljava/lang/reflect/Field;)Ljava/lang/Object;"));
        unbox(Type.getReturnType(desc));
        returnValue();
        mark(old);
    }
}
