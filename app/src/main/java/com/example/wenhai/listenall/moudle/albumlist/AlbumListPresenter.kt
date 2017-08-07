package com.example.wenhai.listenall.moudle.albumlist

import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.utils.LogUtil

internal class AlbumListPresenter(val view: AlbumListContract.View) : AlbumListContract.Presenter {
    companion object {
        const val TAG = "AlbumListPresenter"
    }

    val musicRepository: MusicRepository = MusicRepository()

    init {
        view.setPresenter(this)
    }

    override fun loadNewAlbums() {
        musicRepository.loadNewAlbum(14, object : LoadAlbumCallback {
            override fun onSuccess(albumList: List<Album>) {
                view.setNewAlbums(albumList)
            }

            override fun onFailure() {
                LogUtil.e(TAG, "load new albums failed")
            }
        })
    }
}