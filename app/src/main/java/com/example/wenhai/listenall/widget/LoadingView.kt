package com.example.wenhai.listenall.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView

class LoadingView(context: Context, attributeSet: AttributeSet, refStyle: Int)
    : ImageView(context, attributeSet, refStyle) {
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    lateinit var mRotateAnimator: ObjectAnimator
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        beginLoadingAnimation()
    }


    private fun beginLoadingAnimation() {
        mRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        mRotateAnimator.interpolator = AccelerateDecelerateInterpolator()
        mRotateAnimator.duration = 900
        mRotateAnimator.repeatMode = ObjectAnimator.RESTART
        mRotateAnimator.repeatCount = ObjectAnimator.INFINITE
        mRotateAnimator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRotateAnimator.cancel()
    }
}