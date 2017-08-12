package com.example.wenhai.listenall.utils

import android.content.Context

/**
 * 获取应用信息的工具类
 *
 * Created by Wenhai on 2017/8/4.
 */
object AppUtil {
    @JvmStatic
    fun getAppVersionName(context: Context): String {
        val packageName = context.packageName
        val versionName = context.packageManager.getPackageInfo(packageName, 0).versionName
        return versionName
    }
}