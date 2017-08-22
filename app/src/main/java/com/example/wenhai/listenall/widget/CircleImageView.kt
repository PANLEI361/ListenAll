package com.example.wenhai.listenall.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView


class CircleImageView(context: Context, attributeSet: AttributeSet, refStyle: Int)
    : ImageView(context, attributeSet, refStyle) {
    val path: Path = Path()
    val bitmapPaint = Paint()
    val pathPaint = Paint()
    lateinit var bitmap: Bitmap

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    init {

        bitmapPaint.isAntiAlias = true
        pathPaint.isAntiAlias = true
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeWidth = 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val padding = paddingStart + paddingEnd
        val realWidth = width - padding
        canvas.save()
        path.addCircle(realWidth.toFloat() / 2, realWidth.toFloat() / 2,
                realWidth.toFloat() / 2, Path.Direction.CW)
        canvas.drawPath(path, pathPaint)
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restore()
    }
}