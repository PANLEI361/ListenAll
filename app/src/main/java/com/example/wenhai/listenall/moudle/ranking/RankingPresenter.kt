package com.example.wenhai.listenall.moudle.ranking

import com.example.wenhai.listenall.data.LoadRankingCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Collect

class RankingPresenter(val view: RankingContract.View) : RankingContract.Presenter {

    private val musicRepository = MusicRepository.getInstance(view.getViewContext())

    init {
        view.setPresenter(this)
    }

    override fun loadOfficialRanking(provider: MusicProvider) {
        musicRepository.changeMusicSource(provider, view.getViewContext())
        musicRepository.loadOfficialRanking(provider, object : LoadRankingCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onSuccess(collects: List<Collect>) {
                view.onOfficialRankingLoad(collects)
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }
        })
    }
}
