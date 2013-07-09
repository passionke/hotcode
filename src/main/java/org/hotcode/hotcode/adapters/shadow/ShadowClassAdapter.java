/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.hotcode.hotcode.adapters.shadow;

import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Shadow Class Adapter
 * 
 * @author zhuyong 2013-7-5 09:57:31
 */
public class ShadowClassAdapter extends ClassVisitor {

    private ClassLoader classLoader;

    public ShadowClassAdapter(ClassVisitor cv, ClassLoader classLoader){
        super(Opcodes.ASM4, cv);

        this.classLoader = classLoader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Long index = CRMManager.getIndex(this.classLoader);
        if (index != null) {
            ClassReloaderManager crm = CRMManager.getClassReloaderManager(index);
            name = crm.getClassReloader(crm.getIndex(name)).getShadowClassName();
        }
        super.visit(version, access, name, signature, superName, interfaces);
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
