package com.example.roomviewmodel.asm;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.roomviewmodel.MainActivity;

public class AsmHooK {
    public static Intent getIntent(MainActivity activity) {
        Log.d("xianyu", "call before MainActivity getIntent");
        Activity activity1 = activity;
        return activity1.getIntent();
    }

    public static void fun1(){
        Log.d("xianyu","call Asm HooKedClass fun1");
        HooKedClass.fun1();
    }

    public static void funbefore(){
        Log.d("xianyu","call Asm HooKedClass funbefore");
    }
}
