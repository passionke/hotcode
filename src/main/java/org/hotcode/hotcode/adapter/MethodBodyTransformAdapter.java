package org.hotcode.hotcode.adapter;

import org.hotcode.hotcode.CodeFragment;
import org.hotcode.hotcode.HotCodeGenConstructorMarker;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Transform method body.
 * 
 * @author khotyn.huangt 13-7-20 PM4:12
 */
public class MethodBodyTransformAdapter extends GeneratorAdapter {

    private ClassReloaderManager        classReloaderManager;
    private HotCodeClass                originClass;
    private FieldAccessTransformAdapter fieldAccessTransformAdapter;

    public MethodBodyTransformAdapter(final int access, final Method method, final MethodVisitor mv,
                                      Long classReloaderManagerIndex, Long classReloaderIndex){
        super(access, method, mv);

        classReloaderManager = CRMManager.getClassReloaderManager(classReloaderManagerIndex);

        if (classReloaderIndex != 0) {
            ClassReloader classReloader = classReloaderManager.getClassReloader(classReloaderIndex);
            originClass = classReloader.getOriginClass();
        }

        this.fieldAccessTransformAdapter = new FieldAccessTransformAdapter(access, method, mv,
                                                                           classReloaderManagerIndex,
                                                                           classReloaderIndex);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        fieldAccessTransformAdapter.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            invokeConstructor(owner, name, desc);
        } else if (opcode == Opcodes.INVOKESTATIC) {
            invokeStatic(owner, name, desc);
        } else if (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL) {
            invokeVirtual(owner, name, desc);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    private void invokeConstructor(String owner, String name, String desc) {
        Long index = classReloaderManager.getIndex(owner);

        String hotcodeGenConstructorDescs = Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                     Type.getType(HotCodeGenConstructorMarker.class),
                                                                     Type.INT_TYPE, Type.getType(Object[].class));

        if (index == null || desc.equals(hotcodeGenConstructorDescs)) {
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc);
            return;
        }

        ClassReloader ownerClassReloader = classReloaderManager.getClassReloader(index);
        HotCodeMethod constructor = ownerClassReloader.getLastestClass().getConstructorByNameAndDesc(name, desc);

        if (!ownerClassReloader.getOriginClass().hasConstructor(constructor)) {
            int localIndex = CodeFragment.packArgsToArray(this, desc);
            visitInsn(Opcodes.ACONST_NULL);
            push(constructor.hashCode());
            loadLocal(localIndex);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, hotcodeGenConstructorDescs);
        } else {
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc);
        }
    }

    private void invokeStatic(String owner, String name, String desc) {
        Long index = classReloaderManager.getIndex(owner);

        if (index == null || HotCodeConstant.HOTCODE_ADDED_METHODS.contains(name)) {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, desc);
            return;
        }

        ClassReloader ownerClassReloader = classReloaderManager.getClassReloader(index);

        HotCodeMethod method = ownerClassReloader.getLastestClass().getMethodByNameAndDesc(name, desc);

        if (!ownerClassReloader.getOriginClass().getMethods().contains(method)) {
            int localIndex = CodeFragment.packArgsToArray(this, desc);
            push(HotCodeUtil.getMethodIndex(name, desc));
            loadLocal(localIndex);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, HotCodeConstant.HOTCODE_STATIC_METHOD_ROUTER_NAME,
                                  HotCodeConstant.HOTCODE_STATIC_METHOD_ROUTER_DESC);
            unbox(Type.getReturnType(desc));
        } else {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, desc);
        }
    }

    private void invokeVirtual(String owner, String name, String desc) {
        Long index = classReloaderManager.getIndex(owner);

        if (index == null || HotCodeConstant.HOTCODE_ADDED_METHODS.contains(name)) {
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc);
            return;
        }

        ClassReloader ownerClassReloader = classReloaderManager.getClassReloader(index);

        HotCodeMethod method = ownerClassReloader.getLastestClass().getMethodByNameAndDesc(name, desc);

        if (!ownerClassReloader.getOriginClass().getMethods().contains(method)) {
            int localIndex = CodeFragment.packArgsToArray(this, desc);
            push(HotCodeUtil.getMethodIndex(name, desc));
            loadLocal(localIndex);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, HotCodeConstant.HOTCODE_INSTANCE_METHOD_ROUTER_NAME,
                                  HotCodeConstant.HOTCODE_INSTANCE_METHOD_ROUTER_DESC);
            unbox(Type.getReturnType(desc));
        } else {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, desc);
        }
    }
}
