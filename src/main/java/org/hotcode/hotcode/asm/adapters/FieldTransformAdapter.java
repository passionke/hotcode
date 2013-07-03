package org.hotcode.hotcode.asm.adapters;

import java.util.HashSet;
import java.util.Set;

import org.hotcode.hotcode.*;
import org.hotcode.hotcode.constants.HotCodeConstant;
import org.hotcode.hotcode.structure.FieldsHolder;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
import org.objectweb.asm.*;

import com.google.common.collect.Sets;

/**
 * Replace the field access of a class
 * 
 * @author hotcode.huangt 13-6-26 PM9:32
 */
public class FieldTransformAdapter extends ClassVisitor {

    private ClassReloaderManager classReloaderManager;
    private ClassReloader        classReloader;
    private HotCodeClass         originClass;
    private Set<HotCodeField>    redefiningClassFields = new HashSet<>();

    public FieldTransformAdapter(ClassVisitor cv, long classReloaderManagerIndex, long classReloaderIndex){
        super(Opcodes.ASM4, cv);
        classReloaderManager = HotCode.getClassReloaderManager(classReloaderManagerIndex);
        classReloader = classReloaderManager.getClassReloader(classReloaderIndex);
        originClass = classReloader.getOriginClass();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        redefiningClassFields.add(new HotCodeField(access, name, desc));

        if (originClass.hasField(name, desc)) {
            return super.visitField(access, name, desc, signature, value);
        } else {
            return null;
        }
    }

    @Override
    public MethodVisitor visitMethod(final int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                Long ownerReloaderIndex = classReloaderManager.getIndex(owner);

                if (ownerReloaderIndex != null && !HotCodeConstant.HOTCODE_ADDED_FIELDS.contains(name)
                    && !classReloaderManager.getClassReloader(ownerReloaderIndex).getOriginClass().hasField(name, desc)) {
                    if (opcode == Opcodes.GETSTATIC) {
                        mv.visitFieldInsn(Opcodes.GETSTATIC, owner, HotCodeConstant.HOTCODE_STATIC_FIELDS,
                                          Type.getDescriptor(FieldsHolder.class));
                        mv.visitLdcInsn(HotCodeUtil.getFieldKey(name, desc));
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                           Type.getDescriptor(FieldsHolder.class),
                                           "getField",
                                           Type.getMethodDescriptor(Type.getType(Object.class),
                                                                    Type.getType(String.class)));
                    } else if (opcode == Opcodes.PUTSTATIC) {
                        mv.visitFieldInsn(Opcodes.GETSTATIC, owner, HotCodeConstant.HOTCODE_STATIC_FIELDS,
                                          Type.getDescriptor(FieldsHolder.class));
                        mv.visitInsn(Opcodes.SWAP);
                        mv.visitLdcInsn(HotCodeUtil.getFieldKey(name, desc));
                        mv.visitInsn(Opcodes.SWAP);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                           Type.getDescriptor(FieldsHolder.class),
                                           "addField",
                                           Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class),
                                                                    Type.getType(Object.class)));
                    } else if (opcode == Opcodes.GETFIELD) {
                        CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, owner, HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                                          Type.getDescriptor(FieldsHolder.class));
                        mv.visitLdcInsn(HotCodeUtil.getFieldKey(name, desc));
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                           Type.getDescriptor(FieldsHolder.class),
                                           "getField",
                                           Type.getMethodDescriptor(Type.getType(Object.class),
                                                                    Type.getType(String.class)));
                    } else if (opcode == Opcodes.PUTFIELD) {
                        mv.visitInsn(Opcodes.SWAP);
                        CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, owner, HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                                          Type.getDescriptor(FieldsHolder.class));
                        mv.visitInsn(Opcodes.SWAP);
                        mv.visitLdcInsn(HotCodeUtil.getFieldKey(name, desc));
                        mv.visitInsn(Opcodes.SWAP);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                           Type.getDescriptor(FieldsHolder.class),
                                           "addField",
                                           Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class),
                                                                    Type.getType(Object.class)));
                    }
                } else {
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            }
        };
    }

    @Override
    public void visitEnd() {
        Set<HotCodeField> removedFields = Sets.difference(originClass.getFields(), redefiningClassFields);

        for (HotCodeField field : removedFields) {
            cv.visitField(field.getAccess(), field.getName(), field.getDesc(), null, null);
        }

        super.visitEnd();
    }
}