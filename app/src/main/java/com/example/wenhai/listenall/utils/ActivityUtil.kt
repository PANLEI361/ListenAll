package com.example.wenhai.listenall.utils

/**
 * 在 Activity 中对 Fragment 执行操作的工具类
 *
 * Created by Wenhai on 2017/7/30.
 */

object ActivityUtil {

    @JvmStatic
    fun addFragmentToActivity(fragmentManager: android.support.v4.app.FragmentManager, fragment: android.support.v4.app.Fragment, id: Int) {
        fragmentManager.beginTransaction().add(id, fragment).commit()
    }
}