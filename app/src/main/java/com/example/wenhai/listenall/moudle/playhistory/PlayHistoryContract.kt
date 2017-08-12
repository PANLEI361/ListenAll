package com.example.wenhai.listenall.moudle.playhistory

import android.content.Context
import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.PlayHistory


interface PlayHistoryContract {
    interface View : BaseView<Presenter> {
        fun onPlayHistoryLoad(playHistory: List<PlayHistory>)
        fun onNoPlayHistory()
        fun onFailure(msg: String)
    }

    interface Presenter : BasePresenter {
        fun loadPlayHistory(context: Context)
    }
}