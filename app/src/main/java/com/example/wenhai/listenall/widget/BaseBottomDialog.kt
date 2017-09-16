package com.example.wenhai.listenall.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseBottomDialog(context: Context) : Dialog(context, android.R.style.Theme_Holo_Dialog) {
    lateinit var unbinder: Unbinder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(getLayoutResId())
        unbinder = ButterKnife.bind(this)
        setStyle()
        initView()
    }

    private fun setStyle() {
        //set style
        val window = window
        window.setGravity(Gravity.BOTTOM)
        window.attributes.width = getScreenWidth()
    }

    private fun getScreenWidth(): Int {
        val manager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView()

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unbinder.unbind()
    }
}