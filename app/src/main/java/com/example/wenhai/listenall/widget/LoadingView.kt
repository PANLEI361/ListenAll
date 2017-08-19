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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        beginLoadingAnimation()
    }


    private fun beginLoadingAnimation() {
        val rotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        rotateAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimator.duration = 900
        rotateAnimator.repeatMode = ObjectAnimator.RESTART
        rotateAnimator.repeatCount = ObjectAnimator.INFINITE
        rotateAnimator.start()
    }
}