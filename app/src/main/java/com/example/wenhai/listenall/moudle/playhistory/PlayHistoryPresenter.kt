package com.example.wenhai.listenall.moudle.playhistory

import android.content.Context
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.PlayHistoryDao
import com.example.wenhai.listenall.utils.DAOUtil


internal class PlayHistoryPresenter(val view: PlayHistoryContract.View) : PlayHistoryContract.Presenter {


    private val musicRepository: MusicRepository

    init {
        view.setPresenter(this)
        musicRepository = MusicRepository()
    }

    override fun loadPlayHistory(context: Context) {
        val dao = DAOUtil.getSession(context).playHistoryDao
        val query = dao.queryBuilder()
                .orderDesc(PlayHistoryDao.Properties.PlayTimeInMills)
                .build()
        val playHistoryList = query.list()
        if (playHistoryList.size > 0) {
            view.onPlayHistoryLoad(playHistoryList)
        } else {
            view.onNoPlayHistory()
        }
    }


}