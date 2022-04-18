package com.example.roomviewmodel;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class CallMethodVisitor extends ClassVisitor {

    // Hook 类调用其他类方法
    public static final String sClassNameProxy = "com/example/roomviewmodel/asm/AsmHooK";
    public static final String sActivityMethod = "com/example/roomviewmodel/MainActivity.getIntent()Landroid/content/Intent;";
    public static final String sIntentMethod = "android/app/Activity.getIntent()Landroid/content/Intent;";
    public static final String sHookedClassFun1 = "com/example/roomviewmodel/asm/HooKedClass.fun1()";

    // Hook类定义方法的地方
    public static final String sComponentActivityOnCreate = "androidx/core/app/ComponentActivity.onCreate(Landroid/os/Bundle;)V";
    public static final String sHookedClassFunbefore = "com/example/roomviewmodel/asm/HooKedClass.funbefore()V";

    private static final HashMap<String, String> TOUCH_METHOD = new HashMap<String,String>();
    private static final List<String> SIMPLE_METHOD = new ArrayList();


    static {
        TOUCH_METHOD.put(sActivityMethod, "Lcom/example/roomviewmodel/MainActivity;");
        TOUCH_METHOD.put(sIntentMethod, "Landroid/app/Activity;");

    }

    static {
        SIMPLE_METHOD.add(sHookedClassFun1);
    }

    private String visitName;

    public CallMethodVisitor(int api, ClassVisitor visitor) {
        super(api, visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.visitName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * 只能hook本地依赖的类，Android 运行时的类是无法Hook的，只能HooK调用的地方，通过visitMethodInsn
     * 比如:androidx/core/app/ComponentActivity 可以直接Hook,但是android/app/Activity无法Hook
     */

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        String method = this.visitName + "." + name + desc;

        if(this.visitName.equals("android/app/Activity")){
            System.out.println(">>> this.visitName: "+this.visitName);
        }
        if(this.visitName.equals("androidx/core/app/ComponentActivity")){
            System.out.println(">>> this.visitName: "+this.visitName);
        }

        if(sComponentActivityOnCreate.equals(method)){
            return new LifecycleOnCreateMethodVisitor(methodVisitor);
        }

        if(sHookedClassFunbefore.equals(method)){return new HookedClassMethodVisitor(methodVisitor);}



        return new MethodVisitor(Opcodes.ASM6, methodVisitor) {

            // 这里只能hook方法签名指定的方法，不能hook由于 子类调用父类方法的方法，因为方法签名还是ChildClass.Method
            // owner是在处理类A中调用对象B.fun()时候的B
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                String method = owner + "." + name + desc;
                if (TOUCH_METHOD.containsKey(method)) {
                    System.out.println(">>> "+visitName+" >>> "+method);
                    String className = TOUCH_METHOD.get(method);
                    opcode = Opcodes.INVOKESTATIC;
                    owner = sClassNameProxy;
                    StringBuilder insert = new StringBuilder(desc);
                    insert.insert(1, className);
                    desc = insert.toString();
                    itf = false;
                } else if (SIMPLE_METHOD.contains(method)) {
                    System.out.println("222222222");
                    opcode = Opcodes.INVOKESTATIC;
                    owner = sClassNameProxy;
                    itf = false;
                }


                super.visitMethodInsn(opcode, owner, name, desc, itf);

            }




        };
    }

}
