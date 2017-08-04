package com.example.wenhai.listenall.utils

import android.app.Fragment
import android.app.FragmentManager

/**
 * 在 Activity 中对 Fragment 执行操作的工具类
 *
 * Created by Wenhai on 2017/7/30.
 */

object ActivityUtil {

    @JvmStatic
    fun addFragmentToActivity(fragmentManager: FragmentManager, fragment: Fragment, id: Int) {
        fragmentManager.beginTransaction().add(id, fragment).commit()
    }
}