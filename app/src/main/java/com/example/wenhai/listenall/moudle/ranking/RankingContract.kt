package com.example.wenhai.listenall.moudle.ranking

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Collect

interface RankingContract {
    interface Presenter : BasePresenter {
        fun loadOfficialRanking(provider: MusicProvider)
        fun loadGlobalRanking(ranking: GlobalRanking)
    }

    interface View : BaseView<Presenter> {
        fun onOfficialRankingLoad(collects: List<Collect>)
        fun onGlobalRankingLoad(collect: Collect)
    }

    enum class GlobalRanking {
        BILLBOARD, UK, ORICON
    }
}