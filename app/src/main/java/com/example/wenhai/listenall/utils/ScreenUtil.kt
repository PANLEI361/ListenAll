package com.example.wenhai.listenall.utils

import android.content.Context


object ScreenUtil {
    @JvmStatic
    fun px2dp(context: Context, px: Float): Int {
        val density = context.resources.displayMetrics.density
        return (px / density + 0.5f).toInt()
    }

    @JvmStatic
    fun dp2px(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}