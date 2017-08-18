package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song
import java.io.Serializable

interface DetailContract {

    interface View : BaseView<Presenter> {
        fun setCollectDetail(collect: Collect)
        fun setAlbumDetail(album: Album)
        fun onSongDetailLoaded(song: Song)
        fun onLoadFailed(msg: String)
    }

    interface Presenter : BasePresenter {
        fun loadSongsDetails(id: Long, type: LoadType)
        fun loadSongDetail(song: Song)
    }

    enum class LoadType : Serializable {
        COLLECT, ALBUM, RANKING;
    }

    companion object {
        const val ARGS_ID = "id"
        const val ARGS_COLLECT = "collect"
        const val ARGS_LOAD_TYPE = "type"
    }
}