package com.example.roomviewmodel;

import org.objectweb.asm.MethodVisitor;
        import org.objectweb.asm.Opcodes;

public class HookedClassMethodVisitor extends MethodVisitor {

    public HookedClassMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public void visitCode() {
        //方法执行前插入

        mv.visitLdcInsn("xianyu");
        mv.visitLdcInsn("call Asm HooKedClass funbefore");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        super.visitCode();
        //方法执行后插入
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
