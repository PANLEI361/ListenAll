package com.example.wenhai.listenall.moudle.ranking

import com.example.wenhai.listenall.data.LoadRankingCallback
import com.example.wenhai.listenall.data.LoadSingleRankingCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Collect

class RankingPresenter(val view: RankingContract.View) : RankingContract.Presenter {

    private val musicRepository = MusicRepository.INSTANCE

    init {
        view.setPresenter(this)
    }

    override fun loadOfficialRanking(provider: MusicProvider) {
        musicRepository.changeMusicSource(provider)
        musicRepository.loadOfficialRanking(provider, object : LoadRankingCallback {
            override fun onStart() {

            }

            override fun onSuccess(collects: List<Collect>) {
                view.onOfficialRankingLoad(collects)
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }
        })
    }

    override fun loadGlobalRanking(ranking: RankingContract.GlobalRanking) {
        musicRepository.loadGlobalRanking(ranking, object : LoadSingleRankingCallback {
            override fun onStart() {

            }

            override fun onSuccess(collect: Collect) {
                view.onGlobalRankingLoad(collect)
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }
        })

    }
}