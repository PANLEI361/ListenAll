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
    }

    interface Presenter : BasePresenter {
        fun loadSongsDetails(id: Long, type: Type)
        fun loadSongDetail(song: Song)
    }
}