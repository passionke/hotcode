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
        } else {
            originClass.setClassName(name.replace('/', '.'));
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (isReload) {
            HotCodeField reloadedField = new HotCodeField(access, name, desc);
            if (!originClass.getFields().contains(reloadedField)) {
                reloadedClass.getFields().add(reloadedField);
            }
        } else {
            originClass.getFields().add(new HotCodeField(access, name, desc));
        }
        return super.visitField(access, name, desc, signature, value);
    }
}
