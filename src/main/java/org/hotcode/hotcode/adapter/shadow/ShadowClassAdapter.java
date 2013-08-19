package org.hotcode.hotcode.adapter.shadow;

import java.lang.reflect.Modifier;

import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Shadow Class Adapter
 * 
 * @author zhuyong 2013-7-5 09:57:31
 */
public class ShadowClassAdapter extends ClassVisitor {

    private String shadowClassName;
    private String originClassName;

    public ShadowClassAdapter(ClassVisitor cv, String shadowClassName){
        super(Opcodes.ASM4, cv);
        this.shadowClassName = shadowClassName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        originClassName = name;
        super.visit(version, access, shadowClassName, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // Shadow Class not need <clinit> method.
        if (name.equals("<clinit>")) {
            return null;
        }

        if (!name.equals("<init>") && !Modifier.isStatic(access)) {
            return super.visitMethod(HotCodeUtil.changeAccToPublic(HotCodeUtil.changeAccToStatic(access)), name,
                                     HotCodeUtil.addThisToDesc(desc, originClassName), signature, exceptions);
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
