package com.example.wenhai.listenall.extension

import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.isShowing(): Boolean {
    return visibility == View.VISIBLE
}
