package org.hotcode.hotcode.adapter;

import java.lang.reflect.Modifier;

import org.hotcode.hotcode.HotCodeGenConstructorMarker;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * @author khotyn.huangt 13-7-19 PM6:41
 */
public class ConstructorTransformAdapter extends ClassVisitor {

    private ClassReloader classReloader;

    public ConstructorTransformAdapter(ClassVisitor cv, ClassReloader classReloader){
        super(Opcodes.ASM4, cv);
        this.classReloader = classReloader;
    }

    @Override
    public void visitEnd() {
        HotCodeClass originClass = classReloader.getOriginClass();

        if (!Modifier.isInterface(originClass.getAccess())) {
            String methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
                                                         Type.getType(HotCodeGenConstructorMarker.class),
                                                         Type.INT_TYPE, Type.getType(Object[].class));
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", methodDesc, null, null);
            GeneratorAdapter ga = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC, "<init>", methodDesc);
            ga.visitCode();
            ga.endMethod();
        }

        super.visitEnd();
    }
}
