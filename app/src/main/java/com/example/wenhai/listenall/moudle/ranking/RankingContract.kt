package com.example.wenhai.listenall.moudle.ranking

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Collect

interface RankingContract {
    interface Presenter : BasePresenter {
        fun loadOfficialRanking(provider: MusicProvider)
    }

    interface View : BaseView<Presenter> {
        fun onOfficialRankingLoad(collects: List<Collect>)
    }

    enum class GlobalRanking {
        BILLBOARD, UK, ORICON
    }
}