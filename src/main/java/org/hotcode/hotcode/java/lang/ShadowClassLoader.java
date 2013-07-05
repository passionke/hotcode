/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.hotcode.hotcode.java.lang;

import org.hotcode.hotcode.adapters.shadow.ShadowClassAdapter;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author zhuyong 2013-7-5 3:42:13
 */
public class ShadowClassLoader extends ClassLoader {

    public ShadowClassLoader(ClassLoader parent){
        super(parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Long index = CRMManager.getIndex(getParent());
        ClassReloaderManager classReloadManager = CRMManager.getClassReloaderManager(index);

        ClassReloader classReloader = classReloadManager.getClassReloader(classReloadManager.getIndex(name));

        if (classReloader.getVersionedClassFile() != null) {
            byte[] classFile = classReloader.getVersionedClassFile().getClassFile();
            ClassReader cr = new ClassReader(classFile);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ShadowClassAdapter(cw);
            cr.accept(cv, 0);
            byte[] classRedefined = cw.toByteArray();
            return super.defineClass(name, classRedefined, 0, classRedefined.length);
        }

        return super.findClass(name);
    }
}
