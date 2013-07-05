/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.hotcode.hotcode.adapters.shadow;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Shadow Class Adapter
 * 
 * @author zhuyong 2013-7-5 9:57:31
 */
public class ShadowClassAdapter extends ClassVisitor {

    public ShadowClassAdapter(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }
}
