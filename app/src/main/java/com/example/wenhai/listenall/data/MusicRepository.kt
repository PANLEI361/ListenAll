package com.example.wenhai.listenall.data

import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.onlineprovider.Xiami

internal class MusicRepository() : MusicSource {


    override fun loadCollectDetail(id: Long, callback: LoadCollectDetailCallback) {
        musicSource.loadCollectDetail(id, callback)
    }

    override fun loadAlbumDetail(id: Long, callback: LoadAlbumDetailCallback) {
        musicSource.loadAlbumDetail(id, callback)
    }

    override fun loadSongDetail(song: Song, callback: LoadSongDetailCallback) {
        musicSource.loadSongDetail(song, callback)
    }


    var musicSource: MusicSource

    init {
        //default
        musicSource = Xiami()
    }

    constructor(sourceProvider: MusicProvider) : this() {
        when (sourceProvider) {
            MusicProvider.XIAMI -> {
                musicSource = Xiami()
            }
            MusicProvider.QQMUSIC -> {
//                musicSource = QQ()
            }
            MusicProvider.NETEASE -> {
//                musicSource = NetEase()
            }
        }
    }

    fun changeMusicSource(provider: MusicProvider) {
        when (provider) {
            MusicProvider.XIAMI -> {
                musicSource = Xiami()
            }
            MusicProvider.QQMUSIC -> {
//                musicSource = QQ()
            }
            MusicProvider.NETEASE -> {
//                musicSource = NetEase()
            }
        }
    }

    override fun loadBanner(callback: LoadBannerCallback) {
        musicSource.loadBanner(callback)
    }

    override fun loadHotCollect(count: Int, callback: LoadCollectCallback) {
        musicSource.loadHotCollect(count, callback)
    }

    override fun loadNewAlbum(count: Int, callback: LoadAlbumCallback) {
        musicSource.loadNewAlbum(count, callback)
    }
}


