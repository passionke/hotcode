package org.hotcode.hotcode.adapter;

import java.lang.reflect.Modifier;
import java.util.*;

import org.hotcode.hotcode.CodeFragment;
import org.hotcode.hotcode.HotCodeGenConstructorMarker;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author khotyn.huangt 13-7-19 PM6:41
 */
public class ConstructorTransformAdapter extends ClassVisitor {

    private HotCodeClass                   originClass;
    private Map<HotCodeMethod, MethodNode> addedConstructors = new TreeMap<>(new Comparator<HotCodeMethod>() {

                                                                 @Override
                                                                 public int compare(HotCodeMethod o1, HotCodeMethod o2) {
                                                                     return o1.hashCode() - o2.hashCode();
                                                                 }
                                                             });

    public ConstructorTransformAdapter(ClassVisitor cv, ClassReloader classReloader){
        super(Opcodes.ASM4, cv);
        this.originClass = classReloader.getOriginClass();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name)
            && !originClass.hasConstructor(new HotCodeMethod(access, name, desc, signature, exceptions))) {
            MethodNode addConstructor = new MethodNode(access, name, desc, signature, exceptions);
            addedConstructors.put(new HotCodeMethod(access, name, desc, signature, exceptions), addConstructor);
            return addConstructor;
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        if (!Modifier.isInterface(originClass.getAccess())) {
            String methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
                                                         Type.getType(HotCodeGenConstructorMarker.class),
                                                         Type.INT_TYPE, Type.getType(Object[].class));
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", methodDesc, null, null);
            GeneratorAdapter ga = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC, "<init>", methodDesc);
            ga.visitCode();
            CodeFragment.beforeMethodCheck(mv, Opcodes.ACC_PUBLIC, "<init>", methodDesc,
                                           originClass.getClassName().replace('.', '/'));
            Label defaultLabel = new Label();

            if (addedConstructors.size() > 0) {
                Set<HotCodeMethod> constructors = addedConstructors.keySet();
                int[] keys = new int[constructors.size()];
                Label[] labels = new Label[constructors.size()];

                for (int i = 0; i < constructors.size(); i++) {
                    keys[i] = constructors.hashCode();
                    labels[i] = new Label();
                }

                ga.loadArg(1);
                ga.visitLookupSwitchInsn(defaultLabel, keys, labels);

                Collection<MethodNode> constructorMethodNodes = addedConstructors.values();
                int j = 0;

                for (MethodNode constructorMethodNode : constructorMethodNodes) {
                    ga.mark(labels[j]);

                    Type[] argumentTypes = Type.getArgumentTypes(constructorMethodNode.desc);

                    for (int i = 0; i < argumentTypes.length; i++) {
                        ga.loadArg(2);
                        ga.push(i);
                        ga.arrayLoad(argumentTypes[i]);
                        ga.storeLocal(ga.newLocal(argumentTypes[i]));
                    }

                    constructorMethodNode.instructions.accept(new MethodVisitor(Opcodes.ASM4, mv) {

                        @Override
                        public void visitVarInsn(int opcode, int var) {
                            super.visitIntInsn(opcode, var + 3);
                        }
                    });

                    j++;
                }

                ga.mark(defaultLabel);
            }

            ga.returnValue();
            ga.endMethod();
        }

        super.visitEnd();
    }
}
