package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song

interface DetailContract {

    interface View : BaseView<Presenter> {
        fun setCollectDetail(collect: Collect)
        fun setAlbumDetail(album: Album)
        fun onSongDetailLoaded(song: Song)
        fun onLoadFailed(msg: String)
    }

    interface Presenter : BasePresenter {
        fun loadSongsDetails(id: Long, type: Int)
        fun loadSongDetail(song: Song)
    }

    enum class Type {
        COLLECT, ALBUM
    }
}