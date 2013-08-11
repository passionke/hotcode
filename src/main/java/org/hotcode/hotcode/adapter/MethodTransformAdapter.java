package org.hotcode.hotcode.adapter;

import java.util.Set;

import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Sets;

/**
 * @author khotyn.huangt 13-8-4 PM1:33
 */
public class MethodTransformAdapter extends ClassVisitor {

    private ClassReloader classReloader;

    public MethodTransformAdapter(ClassVisitor cv, ClassReloader classReloader){
        super(Opcodes.ASM4, cv);
        this.classReloader = classReloader;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("<init>") || name.equals("<clinit>")) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        Set<HotCodeMethod> methods = classReloader.getOriginClass().getMethods();
        HotCodeMethod method = new HotCodeMethod(access, name, desc, signature, exceptions);

        if (!methods.contains(method)) {
            return null;
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        Set<HotCodeMethod> originMethods = classReloader.getOriginClass().getMethods();
        Set<HotCodeMethod> currentMethods = classReloader.getLastestClass().getMethods();
        Set<HotCodeMethod> deletedMethods = Sets.difference(originMethods, currentMethods);

        for (HotCodeMethod method : deletedMethods) {
            MethodVisitor mv = cv.visitMethod(method.getAccess(), method.getName(), method.getDesc(),
                                              method.getSignature(), method.getExceptions());
            mv.visitCode();
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        super.visitEnd();
    }
}
