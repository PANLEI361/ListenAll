package com.example.wenhai.listenall.moudle.main.online

import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.utils.LogUtil

internal class OnLinePresenter(var view: OnLineContract.View) : OnLineContract.Presenter {

    var musicRepository: MusicRepository

    init {
        view.setPresenter(this)
        musicRepository = MusicRepository()
    }

    override fun loadBanner(provider: MusicProvider) {
        musicRepository.loadBanner(object : LoadBannerCallback {
            override fun onStart() {
            }

            override fun onSuccess(imgUrlList: List<String>) {
                view.setBanner(imgUrlList)
            }

            override fun onFailure(msg: String) {
                LogUtil.e(TAG, "banner load failed")
            }
        })
    }

    override fun loadHotCollects() {
        musicRepository.loadHotCollect(6, object : LoadCollectCallback {
            override fun onStart() {
            }

            override fun onFailure(msg: String) {
                LogUtil.e(TAG, "hot collect load failed")
            }

            override fun onSuccess(collectList: List<Collect>) {
                view.setHotCollects(collectList)
            }

        })
    }

    override fun loadNewAlbums() {
        musicRepository.loadNewAlbum(6, object : LoadAlbumCallback {
            override fun onStart() {
            }

            override fun onFailure(msg: String) {
                LogUtil.e(TAG, "new albums load failed")
            }

            override fun onSuccess(albumList: List<Album>) {
                view.setNewAlbums(albumList)
            }

        })
    }

    companion object {
        const val TAG = "OnLinePresenter"
    }
}