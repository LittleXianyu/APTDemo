package com.example.roomviewmodel.roomdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.roomviewmodel.R
import com.example.roomviewmodel.arouter.HelloService

@Route(path = "/test/activity_new_word")
class NewWordActivity : AppCompatActivity() {
    private lateinit var editWordView: EditText
    @JvmField
    @Autowired
    public var name: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        ARouter.getInstance().inject(this)

        editWordView = findViewById(R.id.edit_word)
        getIntent()
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
        // 测试ARouter 服务代码
        name?.let { Log.d("xianyu", it) }
        val helloService4 =
            ARouter.getInstance().build("/yourservicegroupname/hello").navigation() as HelloService
        Log.d("xianyu", helloService4.sayHello("Vergil"))
    }
    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
