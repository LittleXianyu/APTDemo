package com.example.roomviewmodel;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import java.util.ArrayList;


public class ClassVisitorFactory {


    private static ArrayList<String> IgnoreClass = new ArrayList<String>();
    static {
        IgnoreClass.add("AsmHooK");
    }

    private static boolean isClassFile(String name) {
        return name != null &&
                name.endsWith(".class") &&
                !name.startsWith("R\\$") &&
                !"R.class".equals(name) &&
                !"BuildConfig.class".equals(name);
    }


    public static boolean shouldVisitClass(String name) {
        if (!isClassFile(name)) {
            return false;
        }

        for(String ignore: IgnoreClass){
            if(name.contains(ignore)){
                 System.out.println("name: "+name);
                return false;
            }
        }
        return true;
    }

    public static ClassVisitor createClassVisitor(ClassWriter classWriter) {

        return new CallMethodVisitor(Opcodes.ASM6, classWriter);
    }
}

