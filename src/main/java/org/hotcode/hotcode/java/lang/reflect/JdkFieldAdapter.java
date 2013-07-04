package org.hotcode.hotcode.java.lang.reflect;

import org.hotcode.hotcode.java.lang.reflect.modifier.GetXModifier;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <pre>
 * 1. Add Field get[X]?() support
 * 2. Add Field set[X]?() support
 * 3. Add declaredAnnotations() support
 * </pre>
 * 
 * @author zhuyong 2013-7-3 11:09:29
 */
public class JdkFieldAdapter extends ClassVisitor {

    public JdkFieldAdapter(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if ((name.equals("get") && desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;"))
            || (name.equals("getBoolean") && desc.equals("(Ljava/lang/Object;)Z"))
            || (name.equals("getByte") && desc.equals("(Ljava/lang/Object;)B"))
            || (name.equals("getChar") && desc.equals("(Ljava/lang/Object;)C"))
            || (name.equals("getShort") && desc.equals("(Ljava/lang/Object;)S"))
            || (name.equals("getInt") && desc.equals("(Ljava/lang/Object;)I"))
            || (name.equals("getLong") && desc.equals("(Ljava/lang/Object;)J"))
            || (name.equals("getFloat") && desc.equals("(Ljava/lang/Object;)F"))
            || (name.equals("getDouble") && desc.equals("(Ljava/lang/Object;)D"))) {
            return new GetXModifier(mv, access, name, desc);
        }

        // if ((name.equals("set") && desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)V"))
        // || (name.equals("setBoolean") && desc.equals("(Ljava/lang/Object;Z)V"))
        // || (name.equals("setByte") && desc.equals("(Ljava/lang/Object;B)V"))
        // || (name.equals("setChar") && desc.equals("(Ljava/lang/Object;C)V"))
        // || (name.equals("setShort") && desc.equals("(Ljava/lang/Object;S)V"))
        // || (name.equals("setInt") && desc.equals("(Ljava/lang/Object;I)V"))
        // || (name.equals("setLong") && desc.equals("(Ljava/lang/Object;J)V"))
        // || (name.equals("setFloat") && desc.equals("(Ljava/lang/Object;F)V"))
        // || (name.equals("setDouble") && desc.equals("(Ljava/lang/Object;D)V"))) {
        // return new SetXModifier(mv, access, name, desc);
        // }
        //
        // if (name.equals("declaredAnnotations") && desc.equals("()Ljava/util/Map;")) {
        // return new DeclaredAnnotationsModifier(mv, access, name, desc);
        // }

        return mv;
    }
}
