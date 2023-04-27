package com.example.coloringtest.model

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import java.util.ArrayDeque

data class PixelModel( var maskId: Int, val pixels : List<Point>){



}

fun getMaskAtPoint(point: Point, masks: List<PixelModel>): PixelModel? {
    for (mask in masks) {
        if (mask.pixels.contains(point)) {
            return mask
        }
    }
    return null
}

fun getBitmapMatrix(bitmap: Bitmap): Array<IntArray> {
    val matrix = Array(bitmap.height) { IntArray(bitmap.width) }
    for (i in 0 until bitmap.height) {
        for (j in 0 until bitmap.width) {
            matrix[i][j] = if (bitmap.getPixel(j, i) == Color.WHITE) 1 else 0
        }
    }
    return matrix
}

fun getMasks(matrix: Array<IntArray>): List<PixelModel> {
    val visited = Array(matrix.size) { BooleanArray(matrix[0].size) }
    val masks = mutableListOf<PixelModel>()
    var maskId = 0
    for (i in 0 until matrix.size) {
        for (j in 0 until matrix[0].size) {
            if (matrix[i][j] == 0 && !visited[i][j]) {
                maskId++
                val queue = ArrayDeque<Point>()
                queue.add(Point(j, i))
                visited[i][j] = true
                val pixels = mutableListOf<Point>()
                while (queue.isNotEmpty()) {
                    val curr = queue.removeFirst()
                    pixels.add(curr)
                    val neighbors = getNeighbors(curr.x, curr.y, matrix.size, matrix[0].size)
                    for (neighbor in neighbors) {
                        if (matrix[neighbor.y][neighbor.x] == 0 && !visited[neighbor.y][neighbor.x]) {
                            queue.add(Point(neighbor.x, neighbor.y))
                            visited[neighbor.y][neighbor.x] = true
                        }
                    }
                }
                masks.add(PixelModel(maskId, pixels))
                for (pixel in pixels) {
                    visited[pixel.y][pixel.x] = true
                }
            }
        }
    }
    return masks
}

fun isMask(mask: List<Point>, matrix: Array<IntArray>): Boolean {
    for (point in mask) {
        val neighbors = getNeighbors(point.x, point.y, matrix.size, matrix[0].size)
        for (neighbor in neighbors) {
            if (matrix[neighbor.y][neighbor.x] == 1) {
                return false
            }
        }
    }
    return true
}

fun getNeighbors(x: Int, y: Int, width: Int, height: Int): List<Point> {
    val result = mutableListOf<Point>()
    for (i in -1..1) {
        for (j in -1..1) {
            if (i == 0 && j == 0) continue
            val newX = x + i
            val newY = y + j
            if (newX in 0 until width && newY in 0 until height) {
                result.add(Point(newX, newY))
            }
        }
    }
    return result
}