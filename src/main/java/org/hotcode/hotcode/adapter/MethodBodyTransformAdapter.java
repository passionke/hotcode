package org.hotcode.hotcode.adapter;

import org.apache.commons.lang.StringUtils;
import org.hotcode.hotcode.CodeFragment;
import org.hotcode.hotcode.HotCodeGenConstructorMarker;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.structure.FieldsHolder;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
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

    private ClassReloaderManager classReloaderManager;
    private HotCodeClass         originClass;

    public MethodBodyTransformAdapter(final int access, final Method method, final MethodVisitor mv,
                                      Long classReloaderManagerIndex, Long classReloaderIndex){
        super(access, method, mv);

        classReloaderManager = CRMManager.getClassReloaderManager(classReloaderManagerIndex);

        if (classReloaderIndex != 0) {
            ClassReloader classReloader = classReloaderManager.getClassReloader(classReloaderIndex);
            originClass = classReloader.getOriginClass();
        }
    }

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
                    getStatic(Type.getObjectType(owner), HotCodeConstant.HOTCODE_STATIC_FIELDS,
                              Type.getType(FieldsHolder.class));
                    push(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                    invokeVirtual(Type.getType(FieldsHolder.class),
                                  new Method("getField", Type.getMethodDescriptor(Type.getType(Object.class),
                                                                                  Type.getType(String.class))));
                    unbox(Type.getType(desc));
                } else if (opcode == Opcodes.PUTSTATIC) {
                    box(Type.getType(desc));
                    getStatic(Type.getObjectType(owner), HotCodeConstant.HOTCODE_STATIC_FIELDS,
                              Type.getType(FieldsHolder.class));
                    swap();
                    push(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                    swap();
                    invokeVirtual(Type.getType(FieldsHolder.class),
                                  new Method("addField", Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                                  Type.getType(String.class),
                                                                                  Type.getType(Object.class))));
                } else if (opcode == Opcodes.GETFIELD) {
                    CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                    getField(Type.getObjectType(owner), HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                             Type.getType(FieldsHolder.class));
                    push(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                    invokeVirtual(Type.getType(FieldsHolder.class),
                                  new Method("getField", Type.getMethodDescriptor(Type.getType(Object.class),
                                                                                  Type.getType(String.class))));
                    unbox(Type.getType(desc));
                } else if (opcode == Opcodes.PUTFIELD) {
                    box(Type.getType(desc));
                    swap();
                    CodeFragment.initHotCodeInstanceFieldIfNull(mv, owner);
                    getField(Type.getObjectType(owner), HotCodeConstant.HOTCODE_INSTANCE_FIELDS,
                             Type.getType(FieldsHolder.class));
                    swap();
                    push(HotCodeUtil.getFieldKey(field.getAccess(), name, desc));
                    swap();
                    invokeVirtual(Type.getType(FieldsHolder.class),
                                  new Method("addField", Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                                  Type.getType(String.class),
                                                                                  Type.getType(Object.class))));
                }
            } else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        } else {
            super.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            Long index = classReloaderManager.getIndex(owner);

            String hotcodeGenConstructorDescs = Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                         Type.getType(HotCodeGenConstructorMarker.class),
                                                                         Type.INT_TYPE, Type.getType(Object[].class));

            if (index == null || desc.equals(hotcodeGenConstructorDescs)) {
                super.visitMethodInsn(opcode, owner, name, desc);
                return;
            }

            ClassReloader ownerClassReloader = classReloaderManager.getClassReloader(index);
            HotCodeMethod constructor = ownerClassReloader.getLastestClass().getConstructorByNameAndDesc(name, desc);

            if (!ownerClassReloader.getOriginClass().hasConstructor(constructor)) {
                Type[] argumentTypes = Type.getArgumentTypes(desc);
                push(argumentTypes.length);
                newArray(Type.getType(Object.class));
                dup();
                int localIndex = newLocal(Type.getType(Object[].class));
                storeLocal(localIndex);

                for (int i = 0; i < argumentTypes.length; i++) {
                    box(argumentTypes[i]);
                    loadLocal(localIndex);
                    swap();
                    push(i);
                    swap();
                    arrayStore(argumentTypes[i]);
                }

                visitInsn(Opcodes.ACONST_NULL);
                push(constructor.hashCode());
                loadLocal(localIndex);
                super.visitMethodInsn(opcode, owner, name, hotcodeGenConstructorDescs);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc);
            }
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
