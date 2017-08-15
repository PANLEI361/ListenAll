package com.example.wenhai.listenall.moudle.artist.list

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.ArtistRegion
import com.example.wenhai.listenall.data.bean.Artist


interface ArtistListContract {
    interface Presenter : BasePresenter {
        fun loadArtists(region: ArtistRegion)

    }

    interface View : BaseView<Presenter> {
        fun onFailure(msg: String)
        fun onArtistsLoad(artists: List<Artist>)

    }
}