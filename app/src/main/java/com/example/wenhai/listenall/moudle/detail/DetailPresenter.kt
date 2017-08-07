package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.data.LoadAlbumDetailCallback
import com.example.wenhai.listenall.data.LoadCollectDetailCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.utils.LogUtil

internal class DetailPresenter(val view: DetailContract.View) : DetailContract.Presenter {

    companion object {
        const val TAG = "DetailPresenter"
    }

    val musicRepository: MusicRepository = MusicRepository()

    init {
        view.setPresenter(this)
    }

    override fun loadDetails(id: Long, type: Type) {
        if (type == Type.COLLECT) {
            musicRepository.loadCollectDetail(id, object : LoadCollectDetailCallback {
                override fun onFailure() {
                    LogUtil.e(TAG, "collect detail load failed")
                }

                override fun onSuccess(collect: Collect) {
                    view.setCollectDetail(collect)
                }

            })
        } else {
            musicRepository.loadAlbumDetail(id, object : LoadAlbumDetailCallback {
                override fun onFailure() {
                    LogUtil.e(TAG, "album detail load failed")
                }

                override fun onSuccess(album: Album) {
                    view.setAlbumDetail(album)
                }

            })
        }
    }

}