package com.example.wenhai.listenall.utils

import java.text.SimpleDateFormat
import java.util.*


fun getDate(mills: Long): String {
    val date = Date(mills * 1000)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(date)
}
