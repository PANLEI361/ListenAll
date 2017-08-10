package com.example.wenhai.listenall.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.example.wenhai.listenall.R

/**
 * 在 Activity 中对 Fragment 执行操作的工具类
 *
 * Created by Wenhai on 2017/7/30.
 */

object FragmentUtil {

    @JvmStatic
    fun addFragmentToActivity(fragmentManager: android.support.v4.app.FragmentManager, fragment: android.support.v4.app.Fragment, id: Int) {
        fragmentManager.beginTransaction().add(id, fragment).commit()
    }

    @JvmStatic
    fun addFragmentToView(fragmentManager: FragmentManager, fragment: Fragment, viewId: Int) {
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(viewId, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    @JvmStatic
    fun addFragmentToMainView(fragmentManager: FragmentManager, fragment: Fragment) {
        addFragmentToView(fragmentManager, fragment, R.id.main_container)
    }

    @JvmStatic
    fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(fragment)
                .commit()
        fragmentManager.popBackStack()
    }
}