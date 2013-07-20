package org.hotcode.hotcode.adapter;

import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.structure.HotCodeField;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

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
    public void visitEnd() {
        if (originClass == null) {
            return;
        }

        for (HotCodeField field : originClass.getFields()) {
            cv.visitField(field.getAccess(), field.getName(), field.getDesc(), null, null);
        }
    }
}
