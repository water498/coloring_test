package com.example.coloringtest.util

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtils {


    fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        // 리사이징에 사용할 Matrix 생성
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        // 원본 비트맵을 Matrix를 사용하여 리사이징
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)

        // 원본 비트맵과 리사이징된 비트맵이 같다면 원본을 반환, 아니라면 리사이징된 비트맵 반환
        return if (resizedBitmap == bitmap) {
            bitmap
        } else {
            bitmap.recycle() // 원본 비트맵은 메모리에서 해제
            resizedBitmap
        }
    }


}