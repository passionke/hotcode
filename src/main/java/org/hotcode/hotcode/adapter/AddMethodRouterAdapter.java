package org.hotcode.hotcode.adapter;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeMethod;
import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.common.collect.Sets;

/**
 * Add method router to class.
 * 
 * @author khotyn.huangt 13-7-28 PM10:10
 */
public class AddMethodRouterAdapter extends ClassVisitor {

    private ClassReloader classReloader;

    public AddMethodRouterAdapter(ClassVisitor cv, ClassReloader classReloader){
        super(Opcodes.ASM4, cv);
        this.classReloader = classReloader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        generateMethodRouter();
        super.visitEnd();
    }

    private void generateMethodRouter() {
        HotCodeClass originClass = classReloader.getOriginClass();
        HotCodeClass latestClass = classReloader.getLastestClass();

        if (Modifier.isInterface(originClass.getAccess())) {
            return;
        }

        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                                                   new Method(HotCodeConstant.HOTCODE_STATIC_METHOD_ROUTER_NAME,
                                                              HotCodeConstant.HOTCODE_STATIC_METHOD_ROUTER_DESC), null,
                                                   null, cv);
        ga.visitCode();

        Set<HotCodeMethod> originMethodSet = originClass.getMethods();
        Set<HotCodeMethod> lastestMethodSet = latestClass.getMethods();

        Set<HotCodeMethod> addedMethodSet = new TreeSet<>(new Comparator<HotCodeMethod>() {

            @Override
            public int compare(HotCodeMethod o1, HotCodeMethod o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        addedMethodSet.addAll(Sets.difference(lastestMethodSet, originMethodSet));

        if (addedMethodSet.size() > 0) {
            Label[] labels = new Label[addedMethodSet.size()];
            int[] keys = new int[addedMethodSet.size()];
            Label defaultLabel = new Label();

            int i = 0;

            for (HotCodeMethod addedMethod : addedMethodSet) {
                labels[i] = new Label();
                keys[i] = HotCodeUtil.getMethodIndex(addedMethod.getName(), addedMethod.getDesc());
                i++;
            }

            ga.loadArg(0);
            ga.visitLookupSwitchInsn(defaultLabel, keys, labels);

            int j = 0;

            for (HotCodeMethod addedMethod : addedMethodSet) {
                ga.mark(labels[j]);
                Type[] argumentTypes = Type.getArgumentTypes(addedMethod.getDesc());

                for (int k = 0; k < argumentTypes.length; k++) {
                    ga.loadArg(1);
                    ga.push(k);
                    ga.arrayLoad(HotCodeUtil.getBoxedType(argumentTypes[k]));
                    ga.unbox(argumentTypes[k]);
                }

                ga.invokeStatic(Type.getObjectType(classReloader.getShadowClassName().replace('.', '/')),
                                new Method(addedMethod.getName(), addedMethod.getDesc()));
                ga.box(Type.getReturnType(addedMethod.getDesc()));
                ga.returnValue();
                j++;
            }

            ga.mark(defaultLabel);
            ga.visitInsn(Opcodes.ACONST_NULL);
            ga.returnValue();
        }

        ga.endMethod();
    }
}
