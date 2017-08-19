package com.example.wenhai.listenall.moudle.main.online

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect

interface OnLineContract {
    interface View : BaseView<Presenter> {
        fun onBannerLoad(imgUrlList: List<String>)
        fun onHotCollectsLoad(hotCollects: List<Collect>)
        fun onNewAlbumsLoad(newAlbums: List<Album>)
    }

    interface Presenter : BasePresenter {
        fun loadBanner(provider: MusicProvider)
        fun loadHotCollects()
        fun loadNewAlbums()
    }
}