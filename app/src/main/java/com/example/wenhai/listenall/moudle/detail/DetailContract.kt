package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect

interface DetailContract {
    interface View : BaseView<Presenter> {
        fun setCollectDetail(collect: Collect)
        fun setAlbumDetail(album: Album)
    }

    interface Presenter : BasePresenter {
        fun loadDetails(id: Long, type: Type)
    }
}