package com.example.coloringtest.util

import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.example.coloringtest.model.getMaskAtPoint

object DeviceUtils {


        // 현재 디바이스의 가로 길이 가져오기
        fun getScreenWidth(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        // 현재 디바이스의 세로 길이 가져오기
        fun getScreenHeight(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }



}