package com.example.wenhai.listenall.data.local

import android.content.Context
import com.example.wenhai.listenall.data.bean.DaoMaster
import org.greenrobot.greendao.database.Database

class DaoOpenHelper(context: Context, name: String)
    : DaoMaster.OpenHelper(context, name) {

    override fun onCreate(db: Database?) {
        super.onCreate(db)
    }

    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        super.onUpgrade(db, oldVersion, newVersion)
    }

}