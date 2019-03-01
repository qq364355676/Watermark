package com.lcx.skiplibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        btn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name","我是library返回的值")
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }

}
