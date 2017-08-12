package com.example.wenhai.listenall.moudle.albumlist

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album

interface AlbumListContract {
    interface Presenter : BasePresenter {
        fun loadNewAlbums()
    }

    interface View : BaseView<Presenter> {
        fun setNewAlbums(albumList: List<Album>)
    }
}