package com.example.wenhai.listenall.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.animation.LinearInterpolator

class ProgressImageButton constructor(context: Context, attrs: AttributeSet?, defStyleRes: Int) : AppCompatImageButton(context, attrs, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    var progress: Float = 0.toFloat()
        set(value) {
            if (value - 0 < 0.0001f || value - 100 > 0.0001f) {
                field = 0f
            } else {
                field = value
            }
            invalidate()
        }
    private var progressStokeWidth: Int = 0
    private val borderStokeWidth: Int = 3
    internal var radiusBorder: Int = 0
    internal var radiusInside: Int = 0
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mDrawable: Drawable? = null
    var progressBounds: RectF = RectF()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mDrawable = drawable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)
        radiusBorder = height / 2 - 18
        radiusInside = radiusBorder - progressStokeWidth

        progressStokeWidth = height / 13 - 1
        progressStokeWidth = if (progressStokeWidth < MIN_PROGRESS_STROKE_WIDTH) MIN_PROGRESS_STROKE_WIDTH else progressStokeWidth
        progressBounds.left = (height / 2 - radiusBorder + progressStokeWidth / 2).toFloat()
        progressBounds.top = (height / 2 - radiusBorder + progressStokeWidth / 2).toFloat()
        progressBounds.right = (height / 2 + radiusBorder - progressStokeWidth / 2).toFloat()
        progressBounds.bottom = (height / 2 + radiusBorder - progressStokeWidth / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        //draw border
        mPaint.strokeWidth = 1f
        mPaint.strokeWidth = borderStokeWidth.toFloat()
        canvas.drawCircle((height / 2).toFloat(), (height / 2).toFloat(), radiusBorder.toFloat(), mPaint)

        //draw progress bar
        mPaint.strokeWidth = progressStokeWidth.toFloat()
        mPaint.strokeCap = Paint.Cap.ROUND
        val swipeDegree: Float = progress / 100f * 360f
        canvas.drawArc(progressBounds, - 90f, swipeDegree, false, mPaint)

        //draw src drawable
        val top = (height / 2 - radiusInside * Math.cos(Math.PI / 4)).toInt()
        val bottom = (top + 2.0 * radiusInside.toDouble() * Math.cos(Math.PI / 4)).toInt()
        val left = (height / 2 - radiusInside * Math.cos(Math.PI / 4)).toInt()
        val right = (left + radiusInside.toDouble() * Math.cos(Math.PI / 4) * 2.0).toInt()
        mDrawable !!.setBounds(left, top, right, bottom)
        mDrawable !!.draw(canvas)
    }


    fun animateProgress(newProgress: Float) {
        val animator = ValueAnimator.ofFloat(progress, newProgress)
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            progress = animation.animatedValue.toString().toFloat()
        }
        animator.duration = 1000
        animator.start()
    }

    fun setDrawable(@DrawableRes resId: Int) {
        mDrawable = context.getDrawable(resId)
        invalidate()
    }

    companion object {
        private val MIN_PROGRESS_STROKE_WIDTH = 5
    }


}
