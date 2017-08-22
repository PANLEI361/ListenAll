package com.example.wenhai.listenall.moudle.main.online

import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect

internal class OnLinePresenter(var view: OnLineContract.View) : OnLineContract.Presenter {

    private var musicRepository: MusicRepository = MusicRepository.INSTANCE

    init {
        view.setPresenter(this)
    }

    override fun loadBanner(provider: MusicProvider) {
        musicRepository.loadBanner(object : LoadBannerCallback {
            override fun onStart() {
            }

            override fun onSuccess(imgUrlList: List<String>) {
                view.onBannerLoad(imgUrlList)
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }
        })
    }

    override fun loadHotCollects() {
        musicRepository.loadHotCollect(1, object : LoadCollectCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(collectList: List<Collect>) {
                view.onHotCollectsLoad(collectList)
            }

        })
    }

    override fun loadNewAlbums() {
        musicRepository.loadNewAlbum(1, object : LoadAlbumCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(albumList: List<Album>) {
                view.onNewAlbumsLoad(albumList)
            }

        })
    }

    companion object {
        const val TAG = "OnLinePresenter"
    }
}