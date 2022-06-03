package com.example.roomviewmodel.arouter

import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter

class RouterHander {

    fun toActivity(view: View, path: String) {
        Log.d("xianyu", "path: " + path)
        ARouter.getInstance().build(path)
            .navigation(view.getContext())
    }
    fun toPaging(view: View) {
        toActivity(view, "/paging/activity")
    }
    fun toViewPager(view: View) {
        toActivity(view, "/viewpager/activity")
    }
    fun toRoom(view: View) {
        toActivity(view, "/test/activity_main")
    }
}
