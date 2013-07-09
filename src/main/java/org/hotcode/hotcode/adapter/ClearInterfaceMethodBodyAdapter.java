package org.hotcode.hotcode.adapter;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author khotyn.huangt 13-7-9 PM4:38
 */
public class ClearInterfaceMethodBodyAdapter extends MethodVisitor {

    private int access;

    public ClearInterfaceMethodBodyAdapter(int access, MethodVisitor mv){
        super(Opcodes.ASM4, mv);
        this.access = access;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (!Modifier.isInterface(access)) {
            super.visitTryCatchBlock(start, end, handler, type);
        }
    }

    @Override
    public void visitLabel(Label label) {
        if (!Modifier.isInterface(access)) {
            super.visitLabel(label);
        }
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        if (!Modifier.isInterface(access)) {
            super.visitFrame(type, nLocal, local, nStack, stack);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (!Modifier.isInterface(access)) {
            super.visitInsn(opcode);
        }
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (!Modifier.isInterface(access)) {
            super.visitVarInsn(opcode, var);
        }
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (!Modifier.isInterface(access)) {
            super.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (!Modifier.isInterface(access)) {
            super.visitIntInsn(opcode, operand);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (!Modifier.isInterface(access)) {
            super.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (!Modifier.isInterface(access)) {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (!Modifier.isInterface(access)) {
            super.visitLdcInsn(cst);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (!Modifier.isInterface(access)) {
            super.visitJumpInsn(opcode, label);
        }
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        if (!Modifier.isInterface(access)) {
            super.visitIincInsn(var, increment);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        if (!Modifier.isInterface(access)) {
            super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (!Modifier.isInterface(access)) {
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (!Modifier.isInterface(access)) {
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (!Modifier.isInterface(access)) {
            super.visitMultiANewArrayInsn(desc, dims);
        }
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (!Modifier.isInterface(access)) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (!Modifier.isInterface(access)) {
            super.visitLineNumber(line, start);
        }
    }
}
