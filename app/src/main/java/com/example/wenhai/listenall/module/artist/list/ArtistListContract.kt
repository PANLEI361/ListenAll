package com.example.wenhai.listenall.module.artist.list

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.ArtistRegion
import com.example.wenhai.listenall.data.bean.Artist


interface ArtistListContract {
    interface Presenter : BasePresenter {
        fun loadArtists(region: ArtistRegion, page: Int)

    }

    interface View : BaseView<Presenter> {
        fun onArtistsLoad(artists: List<Artist>)

    }
}