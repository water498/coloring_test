package com.example.coloringtest.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    protected val TAG = this::class.java.simpleName // BaseActivity를 상속하는 모든 activity에서 각각의 class 명으로 Tag가 바뀜

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}