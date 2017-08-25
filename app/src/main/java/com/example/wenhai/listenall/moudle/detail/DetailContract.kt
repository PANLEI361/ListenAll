package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.moudle.ranking.RankingContract
import java.io.Serializable

interface DetailContract {

    interface View : BaseView<Presenter> {
        fun onCollectDetailLoad(collect: Collect)
        fun onAlbumDetailLoad(album: Album)
        fun onGlobalRankingLoad(collect: Collect)
    }

    interface Presenter : BasePresenter {
        fun loadAlbumDetail(id: Long)
        fun loadCollectDetail(id: Long)
        fun loadSongDetail(id: Long)
        fun loadGlobalRanking(ranking: RankingContract.GlobalRanking)
    }

    enum class LoadType : Serializable {
        SONG, COLLECT, ALBUM,
        GLOBAL_RANKING, OFFICIAL_RANKING;
    }

    companion object {
        const val ARGS_ID = "id"
        const val ARGS_COLLECT = "collect"
        const val ARGS_LOAD_TYPE = "type"
        const val ARGS_GLOBAL_RANKING = "ranking"
    }
}