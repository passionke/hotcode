/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.hotcode.hotcode.adapter.shadow;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Shadow Class Adapter
 * 
 * @author zhuyong 2013-7-5 09:57:31
 */
public class ShadowClassAdapter extends ClassVisitor {

    private String shadowClassname;

    public ShadowClassAdapter(ClassVisitor cv, String shadowClassName){
        super(Opcodes.ASM4, cv);
        this.shadowClassname = shadowClassName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, shadowClassname, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // Shadow Class not need <clinit> method.
        if (name.equals("<clinit>")) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
