package com.example.coloringtest.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.coloringtest.brush.PastelPathEffect
import com.example.coloringtest.model.*
import java.util.*

class ColoringView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var pixelModelsArray: Array<PixelInfoModel>
    private var currentBucket : Int = 0

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap
    private var pixelModels : List<PixelModel> = listOf()
    private val path = Path()
    private val paint = Paint().apply {
        color = Color.RED // 원하는 색상으로 변경
        style = Paint.Style.STROKE // 테두리 스타일로 변경
        strokeWidth = 20f // 두꺼운 선으로 변경
        isAntiAlias = true // 안티 앨리어싱 사용
        strokeJoin = Paint.Join.ROUND // 연결점을 둥글게 처리
        strokeCap = Paint.Cap.ROUND // 끝점을 둥글게 처리
        pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
    }

    private val undoStack = Stack<Path>() // Undo 기능을 위한 Stack
    private val redoStack = Stack<Path>() // Redo 기능을 위한 Stack


    interface OnColoringViewTouchListener{
        fun onActionDown()
        fun onActionMove()
        fun onActionUp()
        fun onActionPointerDown()
    }
    var onColoringViewTouchListener : OnColoringViewTouchListener? = null


    init {
        // remove auto focus view
        isFocusable = true
        isFocusableInTouchMode = true
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("ColoringView","onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        // View 크기가 변경될 때마다 bitmap과 canvas를 생성
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("ColoringView","onDraw")
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)
        // canvas에 bitmap을 그려줌
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        // 현재 그리는 중인 path를 그려줌
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("ColoringView","onTouchEvent")

        val touchX = event.x
        val touchY = event.y

        val strokeWidth = paint.strokeWidth
        val halfStrokeWidth = strokeWidth / 2f
        val left = (touchX - halfStrokeWidth).toInt()
        val top = (touchY - halfStrokeWidth).toInt()
        val right = (touchX + halfStrokeWidth).toInt()
        val bottom = (touchY + halfStrokeWidth).toInt()


        // check current area

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(touchX, touchY)
            MotionEvent.ACTION_MOVE -> touchMove(touchX, touchY)
            MotionEvent.ACTION_UP -> touchUp(touchX, touchY)
            MotionEvent.ACTION_POINTER_DOWN -> { onColoringViewTouchListener?.onActionPointerDown() }
        }
        return true
    }

    fun touchStart(touchX : Float, touchY : Float){
        onColoringViewTouchListener?.onActionDown()

/*        val pixelIndex = touchY * bitmap.width + touchX
        val mPixel = pixelModelsArray[pixelIndex.toInt()]
        currentBucket = mPixel.bucket*/

        // 터치가 시작될 때 경로 (path)를 이동시킴
        path.moveTo(touchX, touchY)
        undoStack.push(Path(path)) // 현재 경로를 undoStack에 저장
        redoStack.clear() // redoStack을 초기화
        invalidate() // View를 다시 그려줌
    }

    fun touchMove(touchX : Float, touchY : Float){
        val pixel = bitmap.getPixel(touchX.toInt(),touchY.toInt())
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        val alpha = Color.alpha(pixel)
        val index = touchY.toInt() * bitmap.width + touchX.toInt()

        Log.d("TAG", "touchMove 실제 커서 위치의 해당 픽셀 : red ${red} green ${green} blue ${blue} alpha ${alpha}")
        Log.d("TAG", "touchMove convert 이후 x,y : ${touchX}, ${touchY}")
        Log.d("TAG", "touchMove convert 이후 index : ${index}")
        Log.d("TAG", "touchMove convert 이후 : ${pixelModelsArray[index.toInt()]}")




        onColoringViewTouchListener?.onActionMove()

/*        val halfStroke = paint.strokeWidth / 2 // stroke 크기의 절반을 구합니다.

        // 현재 터치 이벤트가 발생한 좌표(x,y)를 중심으로 하는 사각형 영역을 구합니다.
        val left = (touchX - halfStroke).toInt()
        val top = (touchY - halfStroke).toInt()
        val right = (touchX + halfStroke).toInt()
        val bottom = (touchY + halfStroke).toInt()

        // 해당 영역 안에 있는 모든 픽셀의 bucket 값을 비교하면서 색칠합니다.
        for (i in left..right) {
            for (j in top..bottom) {
                // 픽셀이 화면을 벗어나지 않는지 확인합니다.
                if (i >= 0 && i < bitmap.width && j >= 0 && j < bitmap.height) {
                    val pixelIndex = j * bitmap.width + i // 현재 좌표의 픽셀 인덱스를 계산합니다.
                    val mPixel = pixelModelsArray[pixelIndex]

                    // 현재 픽셀의 bucket 값이 기존 bucket 값과 같은 경우에만 색칠합니다.
                    if (mPixel.bucket == currentBucket) {
                        bitmap.setPixel(i, j, paint.color)
                    }
                }
            }
        }

        // 색칠된 영역을 화면에 반영합니다.
//        canvas.drawBitmap(bitmap, 0f, 0f, null)
        */

        // 터치가 이동할 때 경로를 그림
        path.lineTo(touchX, touchY)
        invalidate() // View를 다시 그려줌
        Log.d("ColoringView","x : $touchX y : $touchY Mask : ${getMaskAtPoint(Point(touchX.toInt(), touchY.toInt()),pixelModels)?.maskId}")

    }

    fun touchUp(touchX : Float, touchY : Float){
        onColoringViewTouchListener?.onActionUp()
        // 터치가 끝날 때 그리기를 끝냄
//        path.lineTo(touchX, touchY)
        canvas.drawPath(path, paint) // 그린 경로를 실제 bitmap에 그려줌
        path.reset() // 경로를 초기화
        invalidate() // View를 다시 그려줌
    }

    /**
     * Undo 기능
     */
    fun undo() {
        if (!undoStack.empty()) {
            redoStack.push(Path(path)) // 현재 경로를 redoStack에 저장
            undoStack.pop()?.let { undoPath ->
                // undoStack에서 이전 경로를 가져와서 그림
                path.set(undoPath)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                canvas.drawPath(path, paint)
                invalidate() // View를 다시 그려줌
            }
        }
    }

    /**
     * Redo 기능
     */
    fun redo() {
        if (!redoStack.empty()) {
            undoStack.push(Path(path)) // 현재 경로를 undoStack에 저장
            redoStack.pop()?.let { redoPath ->
                // redoStack에서 다음 경로를 가져와서 그림
                path.set(redoPath)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                canvas.drawPath(path, paint)
                invalidate() // View를 다시 그려줌
            }
        }
    }


    fun setImageBitmap(bitmap: Bitmap) {
//        val bitmapMatrix = getBitmapMatrix(bitmap)
//        pixelModels = getMasks(bitmapMatrix)

        Log.d("Tag", "aaaaaa ${BitmapUtils.floodFill(bitmap, 0, 0, 0).contentToString()}")

        pixelModelsArray = Helper.convertBitmapTo2BytePixelArray(bitmap) // 2byte중 하나인 alpha byte를 0, 255 두 개로 모두 바꾸는 전처리
        val afterBitmap = Helper.convertPixelArrayToBitmap(pixelModelsArray, bitmap.width, bitmap.height) // 위에서 변환한 array로 새 bitmap 생성
        pixelModelsArray = Helper.fillAlphaZeroAreasAndReturn(pixelModelsArray, afterBitmap) // 변환된 bitmap을 토대로 bucket값 지정해주는 전처리



        this.bitmap = afterBitmap
        canvas = Canvas(afterBitmap)
        invalidate() // 화면 갱신
    }

    fun setStrokeWidth(width : Float){
        paint.strokeWidth = width
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    // Paint 객체에 커스텀 브러시 설정
    fun setShader(shader: Shader?){
        paint.shader = shader
    }


}

