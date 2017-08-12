package com.example.wenhai.listenall.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {
    @JvmStatic
    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}