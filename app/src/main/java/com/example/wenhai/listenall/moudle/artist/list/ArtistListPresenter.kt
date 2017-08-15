package com.example.wenhai.listenall.moudle.artist.list

import com.example.wenhai.listenall.data.ArtistRegion
import com.example.wenhai.listenall.data.LoadArtistsCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Artist

internal class ArtistListPresenter(val view: ArtistListContract.View) : ArtistListContract.Presenter {
    val musicRepository: MusicRepository = MusicRepository()

    init {
        view.setPresenter(this)
    }

    override fun loadArtists(region: ArtistRegion) {
        musicRepository.loadArtists(region, object : LoadArtistsCallback {
            override fun onFailure() {
                view.onFailure("获取艺人列表失败")
            }

            override fun onSuccess(artists: List<Artist>) {
                view.onArtistsLoad(artists)
            }

        })
    }
}