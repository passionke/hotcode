package org.hotcode.hotcode.adapter;

import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Collect the basic info of a class
 * 
 * @author khotyn 13-6-26 PM9:19
 */
public class ClassInfoCollectAdapter extends ClassVisitor {

    private HotCodeClass originClass;
    private HotCodeClass reloadedClass;

    private boolean      isReload;

    public ClassInfoCollectAdapter(ClassVisitor cv, ClassReloader classReloader, boolean isReload){
        super(Opcodes.ASM4, cv);
        this.originClass = classReloader.getOriginClass();
        this.reloadedClass = new HotCodeClass();
        this.isReload = isReload;
        classReloader.setReloadedClass(this.reloadedClass);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (isReload) {
            reloadedClass.setClassName(originClass.getClassName());
            reloadedClass.setAccess(access);
        } else {
            originClass.setClassName(name.replace('/', '.'));
            originClass.setAccess(access);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        int newAccess = access;

        if ((access & Opcodes.ACC_FINAL) != 0) {
            newAccess = newAccess - Opcodes.ACC_FINAL;
        }

        if (isReload) {
            HotCodeField reloadedField = new HotCodeField(access, name, desc);
            if (!originClass.getFields().contains(reloadedField)) {
                reloadedClass.getFields().add(reloadedField);
            }
        } else {
            originClass.getFields().add(new HotCodeField(newAccess, name, desc));
        }
        return super.visitField(newAccess, name, desc, signature, value);

    }
}
