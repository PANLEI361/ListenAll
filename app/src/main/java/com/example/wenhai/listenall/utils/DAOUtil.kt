package com.example.wenhai.listenall.utils

import android.content.Context
import com.example.wenhai.listenall.data.bean.DaoMaster
import com.example.wenhai.listenall.data.bean.DaoSession


object DAOUtil {

    const val TAG = "DAOUtil"
    const val DATABASE_NAME = "ListenAll.db"

    @JvmStatic
    private var daoSession: DaoSession? = null

    @JvmStatic
    fun getSession(context: Context): DaoSession {
        if (daoSession == null) {
            synchronized(DAOUtil::class.java) {
                if (daoSession == null) {
                    val devHelper = DaoMaster.newDevSession(context, DATABASE_NAME)
                    val daoMaster = DaoMaster(devHelper.database)
                    daoSession = daoMaster.newSession()
                }
            }
        }
        return daoSession !!

    }
}