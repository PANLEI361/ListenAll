package com.example.wenhai.listenall.utils

import android.util.Log

/**
 * Created by Wenhai on 2017/7/30.
 */
const val isDebug = true

object LogUtil {

    @JvmStatic
    fun d(tag: String, msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }

    }
}