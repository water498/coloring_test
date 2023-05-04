package com.example.coloringtest.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import java.util.*
import kotlin.math.pow

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

    // Queue Linear Flood Fill 알고리즘을 사용하여 처리하는 함수
    fun floodFill(bitmap: Bitmap, x: Int, y: Int, bucket: Int): IntArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val queue: Queue<Int> = LinkedList<Int>()
        val oldBucket = pixels[y * width + x] and 0xFF // 마지막 1byte만 사용하여 bucket값 추출
        if (oldBucket == bucket) {
            return intArrayOf() // bucket값이 이미 같은 경우 아무것도 하지 않음
        }
        queue.add(y * width + x)
        while (!queue.isEmpty()) {
            val p = queue.remove()
            if (p < 0 || p >= width * height) {
                continue
            }
            if (pixels[p] and 0xFF != oldBucket) { // 마지막 1byte만 사용하여 bucket값 추출
                continue
            }
            pixels[p] = pixels[p] and 0xFFFFFF00.toInt() or bucket // 마지막 1byte만 제거하여 bucket값 설정
            val x = p % width
            val y = p / width
            queue.add((y - 1) * width + x) // 위쪽 픽셀 추가
            queue.add((y + 1) * width + x) // 아래쪽 픽셀 추가
            queue.add(y * width + (x - 1)) // 왼쪽 픽셀 추가
            queue.add(y * width + (x + 1)) // 오른쪽 픽셀 추가
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return pixels.map { it and 0xFF }.toIntArray() // 마지막 1byte만 추출하여 1차원 배열로 반환
    }

    fun makeCircleBitmap(){
        // 200x200 크기의 행렬을 만들고 모든 요소를 0으로 초기화
        val matrix = Array(200) { IntArray(200) }

        // 검정색 원 그리기
        val center_x = 100
        val center_y = 100
        val radius = 50

        for (i in 0 until 200) {
            for (j in 0 until 200) {
                // 현재 좌표와 원의 중심 사이의 거리 계산
                val dist = kotlin.math.sqrt((i - center_x).toDouble().pow(2.0) + (j - center_y).toDouble().pow(2.0))
                if (dist <= radius + 1.5 && dist >= radius - 1.5) {
                    // 현재 좌표와 원의 중심 사이의 거리가 원의 반지름 +- 1.5 이내이면
                    // 검정색으로 설정
                    matrix[i][j] = 255
                }
            }
        }

        // 영역별로 숫자 할당하기
        for (i in 0 until 200) {
            for (j in 0 until 200) {
                if (matrix[i][j] == 255) {
                    // 현재 좌표가 원 안쪽이면
                    matrix[i][j] = 0
                } else {
                    // 현재 좌표가 원 바깥쪽이면
                    matrix[i][j] = 1
                }
            }
        }

        // 1차원 배열로 변환하기
        val array = mutableListOf<Byte>()
        for (i in 0 until 200) {
            for (j in 0 until 200) {
                // alpha값은 항상 255로 설정
                array.add(255.toByte())

                // bucket값에 따라서 0 또는 1로 설정
                array.add(matrix[i][j].toByte())
            }
        }

        // 배열 출력하기
        Log.d("TAG", "make Circle Bitmap : ${array.toString()}")
    }

}