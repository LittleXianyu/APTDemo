package com.example.roomviewmodel.roomdemo

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WordsApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { WordRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WordRepository(database.wordDao()) }
    override fun onCreate() {
        super.onCreate()
        // if (isDebug(this)) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
        ARouter.openLog() // 打印日志
        ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.printStackTrace()
        // }//
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }
    fun isDebug(context: Context): Boolean {
        return context.getApplicationInfo() != null &&
            context.getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE !== 0
    }
}
