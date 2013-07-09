package org.hotcode.hotcode.adapter;

import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Generate assist class.
 * 
 * @author khotyn.huangt 13-7-8 PM5:18
 */
public class AssistClassAdapter extends ClassVisitor {

    private String assistClassName;

    public AssistClassAdapter(ClassVisitor cv, String assistClassName){
        super(Opcodes.ASM4, cv);
        this.assistClassName = assistClassName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        int newAccess = Modifier.isInterface(access) ? access - Opcodes.ACC_INTERFACE : access;

        super.visit(version, newAccess, assistClassName, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (StringUtils.equals(name, "<clinit>")) {
            return super.visitMethod(access | Opcodes.ACC_PUBLIC, HotCodeConstant.HOTCODE_INTERFACE_CLINIT_METHOD_NAME,
                                     desc, signature, exceptions);
        }

        return null;
    }
}
