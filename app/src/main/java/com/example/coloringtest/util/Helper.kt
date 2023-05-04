package com.example.coloringtest.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.coloringtest.model.PixelInfoModel
import java.lang.Math.abs
import java.util.*

object Helper {

    val tolerance = 30

    fun convertBitmapTo2BytePixelArray(bitmap: Bitmap): Array<PixelInfoModel> {
        val pixelArray = Array<PixelInfoModel>(bitmap.width * bitmap.height) { PixelInfoModel(0, 0) }

        for (j in 0 until bitmap.height) {
            for (i in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(i, j)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val alpha = Color.alpha(pixel)

                val myAlpha = if (red == 0 && green == 0 && blue == 0 && alpha == 0 ||
                    (red >= 255-tolerance && green >= 255-tolerance && blue >= 255-tolerance && alpha >= 255-tolerance)) {
                    0 // 흰색일경우 -> 0
                } else {
                    255 // else 검정일경우 -> 255
                }
                val bucket = 0

                pixelArray[j * bitmap.width + i] = PixelInfoModel(myAlpha, bucket)
            }
        }

        return pixelArray
    }


    fun convertPixelArrayToBitmap(pixelInfoArray: Array<PixelInfoModel>, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (j in 0 until height) {
            for (i in 0 until width) {
                val index = j * width + i
                val pixel = pixelInfoArray[index]
                val alpha = pixel.alpha
                val color = Color.argb(alpha, 0, 0, 0)
                bitmap.setPixel(i, j, color)
            }
        }

        return bitmap
    }


    fun fillAlphaZeroAreasAndReturn(pixelArray: Array<PixelInfoModel>, bitmap: Bitmap): Array<PixelInfoModel> {
        val width = bitmap.width
        val height = bitmap.height
        var currentBucket = 1

        // Flood fill algorithm
        for (j in 0 until height) {
            for (i in 0 until width) {
                val pixelIndex = j * width + i
                val pixel = pixelArray[pixelIndex]

                // Skip if already processed or not alpha zero
                if (pixel.bucket != 0 || pixel.alpha != 0) continue

                // Find new alpha zero area, mark with new bucket number and process it
                pixel.bucket = currentBucket
                val queue: Queue<Int> = LinkedList()
                queue.add(pixelIndex)

                while (queue.isNotEmpty()) {
                    val currentIndex = queue.poll()
                    val x = currentIndex % width
                    val y = currentIndex / width

                    // Check 4-connected neighbours for unprocessed alpha zero pixels
                    if (x > 0 && pixelArray[currentIndex - 1].bucket == 0 && pixelArray[currentIndex - 1].alpha == 0) {
                        pixelArray[currentIndex - 1].bucket = currentBucket
                        queue.add(currentIndex - 1)
                    }
                    if (x < width - 1 && pixelArray[currentIndex + 1].bucket == 0 && pixelArray[currentIndex + 1].alpha == 0) {
                        pixelArray[currentIndex + 1].bucket = currentBucket
                        queue.add(currentIndex + 1)
                    }
                    if (y > 0 && pixelArray[currentIndex - width].bucket == 0 && pixelArray[currentIndex - width].alpha == 0) {
                        pixelArray[currentIndex - width].bucket = currentBucket
                        queue.add(currentIndex - width)
                    }
                    if (y < height - 1 && pixelArray[currentIndex + width].bucket == 0 && pixelArray[currentIndex + width].alpha == 0) {
                        pixelArray[currentIndex + width].bucket = currentBucket
                        queue.add(currentIndex + width)
                    }
                }

                currentBucket++
            }
        }

        return pixelArray
    }

}