package com.example.roomviewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.roomviewmodel.arouter.RouterHander
import com.example.roomviewmodel.databinding.ActivityHomeBindingImpl

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityHomeBindingImpl = DataBindingUtil.setContentView(
            this, R.layout.activity_home
        )
        binding.routerHandler = RouterHander()
    }
}
