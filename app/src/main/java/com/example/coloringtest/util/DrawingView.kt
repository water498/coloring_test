package com.example.coloringtest.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DrawingView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {
    private val mPaintSrcIn =
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val mPaintDstIn =
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val mPaintColor = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintEraser = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mMatrix = Matrix()
    private val mLayerCanvas = Canvas()
    private var mInnerShape: Bitmap? = null
    private var mOuterShape: Bitmap? = null
    private var mLayerBitmap: Bitmap? = null
    private val mDrawOps = ArrayList<DrawOp>()
    private val mCurrentOp = DrawOp()
    private val mUndoneOps = ArrayList<DrawOp>()

    init {
        mPaintSrcIn.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mPaintDstIn.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mPaintColor.style = Paint.Style.STROKE
        mPaintColor.strokeJoin = Paint.Join.ROUND
        mPaintColor.strokeCap = Paint.Cap.ROUND
        mPaintEraser.set(mPaintColor)
        mPaintEraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mPaintEraser.maskFilter = BlurMaskFilter(
            resources
                .displayMetrics.density * 4, BlurMaskFilter.Blur.NORMAL
        )
    }

    fun setShape(inner: Int, outer: Int) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ALPHA_8
        setShape(
            BitmapFactory.decodeResource(resources, inner, options),
            BitmapFactory.decodeResource(resources, outer, options)
        )
    }

    fun setShape(inner: Bitmap?, outer: Bitmap?) {
        mInnerShape = inner
        mOuterShape = outer
        requestLayout()
        invalidate()
    }

    fun setDrawingColor(color: Int) {
        mCurrentOp.reset()
        mCurrentOp.type = DrawOp.Type.PAINT
        mCurrentOp.color = color
    }

    fun setDrawingStroke(stroke: Int) {
        mCurrentOp.reset()
        mCurrentOp.type = DrawOp.Type.PAINT
        mCurrentOp.stroke = stroke
    }

    fun enableEraser() {
        mCurrentOp.reset()
        mCurrentOp.type = DrawOp.Type.ERASE
    }

    fun clearDrawing() {
        mDrawOps.clear()
        mUndoneOps.clear()
        mCurrentOp.reset()
        invalidate()
    }

    fun undoOperation() {
        if (mDrawOps.size > 0) {
            val last = mDrawOps.removeAt(mDrawOps.size - 1)
            mUndoneOps.add(last)
            invalidate()
        }
    }

    fun redoOperation() {
        if (mUndoneOps.size > 0) {
            val redo = mUndoneOps.removeAt(mUndoneOps.size - 1)
            mDrawOps.add(redo)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mLayerBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mLayerCanvas.setBitmap(mLayerBitmap)
        if (mOuterShape != null) {
            val dx = (w - mOuterShape!!.width) / 2
            val dy = (h - mOuterShape!!.height) / 2
            mMatrix.setTranslate(dx.toFloat(), dy.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }

        // NOTE: Without extra bitmap or layer.. but HW Acceleration does not support setMaskFilter which means
        // eraser has strong edges whilst drawing.
        // @see http://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
        /*
		canvas.drawBitmap(mOuterShape, 0, 0, null);
		canvas.saveLayer(null, mPaint, Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		canvas.drawBitmap(mInnerShape, 0, 0, null);
		canvas.saveLayer(null, mPaintSrcIn, Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
		canvas.drawBitmap(mBitmapDraw, 0, 0, null);
		canvas.drawPath(mPath, mPaintDraw);
		canvas.restore();
		canvas.restore();
		*/

        // Clear software canvas
        mLayerCanvas.drawColor(0, PorterDuff.Mode.CLEAR)

        // Draw picture from ops
        for (op in mDrawOps) {
            drawOp(mLayerCanvas, op)
        }
        drawOp(mLayerCanvas, mCurrentOp)

        // Mask the drawing to the inner surface area of the shape
        mLayerCanvas.drawBitmap(mInnerShape!!, mMatrix, mPaintDstIn)

        // Draw orignal shape to view
        canvas.drawBitmap(mOuterShape!!, mMatrix, null)

        // Draw masked image to view
        canvas.drawBitmap(mLayerBitmap!!, 0f, 0f, null)
    }

    private fun drawOp(canvas: Canvas, op: DrawOp) {
        if (op.path.isEmpty) {
            return
        }
        val paint: Paint
        if (op.type == DrawOp.Type.PAINT) {
            paint = mPaintColor
            paint.color = op.color
            paint.strokeWidth = op.stroke.toFloat()
        } else {
            paint = mPaintEraser
            paint.strokeWidth = op.stroke.toFloat()
        }
        mLayerCanvas.drawPath(op.path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mUndoneOps.clear()
                mCurrentOp.path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < event.historySize) {
                    mCurrentOp.path.lineTo(event.getHistoricalX(i), event.getHistoricalY(i))
                    i++
                }
                mCurrentOp.path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrentOp.path.lineTo(x, y)
                mDrawOps.add(DrawOp(mCurrentOp))
                mCurrentOp.path.reset()
            }
        }
        invalidate()
        return true
    }

    private class DrawOp {
        val path = Path()
        var type: Type? = null
        var color = 0
        var stroke = 0

        constructor() {
            //
        }

        fun reset() {
            path.reset()
        }

        constructor(op: DrawOp) {
            path.set(op.path)
            type = op.type
            color = op.color
            stroke = op.stroke
        }

        enum class Type {
            PAINT, ERASE
        }
    } /*
	@Override
	protected Parcelable onSaveInstanceState()
	{
		return new SavedState(super.onSaveInstanceState(), mBitmapDraw);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState savedState = (SavedState)state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if(savedState.bitmap != null){
			mBitmapDraw = savedState.bitmap;
			mCanvasDraw = new Canvas(mBitmapDraw);
			invalidate();
		}
	}


	static class SavedState extends BaseSavedState
	{
		private Bitmap bitmap;


		public SavedState(Parcelable superState, Bitmap bitmap)
		{
			super(superState);
			this.bitmap = bitmap;
		}

		public SavedState(Parcel source)
		{
			super(source);
			bitmap = source.readParcelable(getClass().getClassLoader());
		}

		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			dest.writeParcelable(bitmap, flags);
		}


		public static final Parcelable.Creator<SavedState>
			CREATOR = new Parcelable.Creator<SavedState>()
		{
			public SavedState createFromParcel(Parcel in)
			{
				return new SavedState(in);
			}

			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
		};
	}
	*/
}
