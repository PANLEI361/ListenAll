package com.example.wenhai.listenall.data

import com.example.wenhai.listenall.data.online.XiaMiMusicSource

/**
 * Created by Wenhai on 2017/8/4.
 */
internal class MusicRepository() : MusicSource {


    var musicSource: MusicSource

    init {
        //default
        musicSource = XiaMiMusicSource()
    }

    constructor(sourceSupplier: MusicSupplier) : this() {
        when (sourceSupplier) {
            MusicSupplier.XIAMI -> {
                musicSource = XiaMiMusicSource()
            }
            MusicSupplier.QQMUSIC -> {
//                musicSource = QQMusicSource()
            }
            MusicSupplier.NETEASE -> {
//                musicSource = NetEaseMusicSource()
            }
        }
    }

    fun changeMusicSource(supplier: MusicSupplier) {
        when (supplier) {
            MusicSupplier.XIAMI -> {
                musicSource = XiaMiMusicSource()
            }
            MusicSupplier.QQMUSIC -> {
//                musicSource = QQMusicSource()
            }
            MusicSupplier.NETEASE -> {
//                musicSource = NetEaseMusicSource()
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


