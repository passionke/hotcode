package org.hotcode.hotcode.jdk;

import org.hotcode.hotcode.HotCode;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * @author khotyn.huangt 13-8-4 PM3:48
 */
public class URLClassLoaderAdapter extends ClassVisitor {

    public URLClassLoaderAdapter(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("findClass")) {
            return new MethodVisitor(Opcodes.ASM4, mv) {

                @Override
                public void visitCode() {
                    GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, desc);
                    ga.loadArg(0);
                    ga.invokeStatic(Type.getType(HotCode.class),
                                    new Method("findClass", Type.getMethodDescriptor(Type.getType(Class.class),
                                                                                     Type.getType(String.class))));
                    ga.dup();
                    Label label = new Label();
                    ga.ifNull(label);
                    ga.returnValue();
                    ga.mark(label);
                    ga.pop();
                    super.visitCode();
                }
            };
        }
        return mv;
    }
}
