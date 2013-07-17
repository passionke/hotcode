package org.hotcode.hotcode.adapter;

import org.apache.commons.lang.StringUtils;
import org.hotcode.hotcode.CodeFragment;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.structure.FieldsHolder;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * Replace the field access of a class
 * 
 * @author khotyn 13-6-26 PM9:32
 */
public class FieldTransformAdapter extends ClassVisitor {

    private ClassReloaderManager classReloaderManager;
    private HotCodeClass         originClass;

    public FieldTransformAdapter(ClassVisitor cv, long classReloaderManagerIndex, long classReloaderIndex){
        super(Opcodes.ASM4, cv);

        classReloaderManager = CRMManager.getClassReloaderManager(classReloaderManagerIndex);

        if (classReloaderIndex != 0) {
            ClassReloader classReloader = classReloaderManager.getClassReloader(classReloaderIndex);
            originClass = classReloader.getOriginClass();
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature,
                                     String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {

            GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, desc);

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                Long ownerReloaderIndex = classReloaderManager.getIndex(owner);

                if (ownerReloaderIndex != null) {
                    ClassReloader ownerClassReloader = classReloaderManager.getClassReloader(ownerReloaderIndex);

                    if (originClass != null && !StringUtils.equals(owner.replace('/', '.'), originClass.getClassName())) {
                        ownerClassReloader.checkAndReload();
                    }

                    HotCodeClass ownerOriginClass = ownerClassReloader.getOriginClass();
                    HotCodeClass ownerLastestClass = ownerClassReloader.getLastestClass();

                    HotCodeField field = ownerLastestClass.getFieldByName(name);

                    if (!HotCodeConstant.HOTCODE_ADDED_FIELDS.contains(name) && !ownerOriginClass.hasField(field)) {
                        if (opcode == Opcodes.GETSTATIC) {
                            ga.visitFieldInsn(Opcodes.GETSTATIC, owner, HotCodeConstant.HOTCODE_STATIC_FIELDS,
                                              Type.getDescriptor(FieldsHolder.class));
                            ga.visitLdcInsn(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                            ga.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                               Type.getDescriptor(FieldsHolder.class),
                                               "getField",
                                               Type.getMethodDescriptor(Type.getType(Object.class),
                                                                        Type.getType(String.class)));
                            ga.unbox(Type.getType(desc));
                        } else if (opcode == Opcodes.PUTSTATIC) {
                            ga.box(Type.getType(desc));
                            ga.visitFieldInsn(Opcodes.GETSTATIC, owner, HotCodeConstant.HOTCODE_STATIC_FIELDS,
                                              Type.getDescriptor(FieldsHolder.class));
                            ga.visitInsn(Opcodes.SWAP);
                            ga.visitLdcInsn(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                            ga.visitInsn(Opcodes.SWAP);
                            ga.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getDescriptor(FieldsHolder.class),
                                               "addField", Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                                    Type.getType(String.class),
                                                                                    Type.getType(Object.class)));
                        } else if (opcode == Opcodes.GETFIELD) {
                            CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                            ga.visitFieldInsn(Opcodes.GETFIELD, owner, HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                                              Type.getDescriptor(FieldsHolder.class));
                            ga.visitLdcInsn(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                            ga.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                               Type.getDescriptor(FieldsHolder.class),
                                               "getField",
                                               Type.getMethodDescriptor(Type.getType(Object.class),
                                                                        Type.getType(String.class)));
                            ga.unbox(Type.getType(desc));
                        } else if (opcode == Opcodes.PUTFIELD) {
                            ga.box(Type.getType(desc));
                            ga.visitInsn(Opcodes.SWAP);
                            CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                            ga.visitFieldInsn(Opcodes.GETFIELD, owner, HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                                              Type.getDescriptor(FieldsHolder.class));
                            ga.visitInsn(Opcodes.SWAP);
                            ga.visitLdcInsn(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                            ga.visitInsn(Opcodes.SWAP);
                            ga.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getDescriptor(FieldsHolder.class),
                                               "addField", Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                                    Type.getType(String.class),
                                                                                    Type.getType(Object.class)));
                        }
                    } else {
                        super.visitFieldInsn(opcode, owner, name, desc);
                    }
                } else {
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            }
        };
    }

    @Override
    public void visitEnd() {
        if (originClass == null) {
            return;
        }

        for (HotCodeField field : originClass.getFields()) {
            cv.visitField(field.getAccess(), field.getName(), field.getDesc(), null, null);
        }
    }
}
