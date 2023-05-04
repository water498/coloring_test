package com.example.coloringtest.brush

import android.graphics.*
import kotlin.math.*

class PastelPathEffect(
    private val brushSize: Float,
    private val brushColor: Int
) : PathEffect() {
    companion object {
        private const val SEGMENT_LENGTH = 8
    }

    fun onCreateStrokedPath(path: Path?): Path {
        val newPath = Path()
        path?.let {
            val pathMeasure = PathMeasure(path, false)
            var pos = 0f
            val coords = FloatArray(2)
            while (pos < pathMeasure.length) {
                // get the starting point of each segment
                pathMeasure.getPosTan(pos, coords, null)

                // create a new path that approximates each segment with a curve
                val segmentPath = Path()
                segmentPath.moveTo(coords[0], coords[1])
                val endPos = min(pos + SEGMENT_LENGTH, pathMeasure.length)
                while (pos < endPos) {
                    pos += SEGMENT_LENGTH
                    pathMeasure.getPosTan(pos, coords, null)
                    segmentPath.lineTo(coords[0], coords[1])
                }
                newPath.addPath(segmentPath)
            }
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = brushColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = brushSize
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        newPath.transform(Matrix().apply { setRotate(-45f) })
        newPath.transform(Matrix().apply { setScale(1f, 0.5f) })
        newPath.transform(Matrix().apply { setTranslate(-brushSize, brushSize) })
        newPath.transform(Matrix().apply { setRotate(45f) })
        return newPath
    }
}