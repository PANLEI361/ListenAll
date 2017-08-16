package com.example.wenhai.listenall.data

import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.onlineprovider.Xiami

internal class MusicRepository() : MusicSource {


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

    override fun loadSearchRecommend(keyword: String, callback: LoadSearchRecommendCallback) {
        musicSource.loadSearchRecommend(keyword, callback)
    }

    override fun searchByKeyword(keyword: String, callback: LoadSearchResultCallback) {
        musicSource.searchByKeyword(keyword, callback)
    }


    override fun loadCollectDetail(id: Long, callback: LoadCollectDetailCallback) {
        musicSource.loadCollectDetail(id, callback)
    }

    override fun loadAlbumDetail(id: Long, callback: LoadAlbumDetailCallback) {
        musicSource.loadAlbumDetail(id, callback)
    }

    override fun loadSongDetail(song: Song, callback: LoadSongDetailCallback) {
        musicSource.loadSongDetail(song, callback)
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

    override fun loadArtists(region: ArtistRegion, callback: LoadArtistsCallback) {
        musicSource.loadArtists(region, callback)
    }

    override fun loadArtistDetail(artist: Artist, callback: LoadArtistDetailCallback) {
        musicSource.loadArtistDetail(artist, callback)
    }

    override fun loadArtistHotSongs(artist: Artist, callback: LoadArtistHotSongsCallback) {
        musicSource.loadArtistHotSongs(artist, callback)
    }

    override fun loadArtistAlbums(artist: Artist, callback: LoadArtistAlbumsCallback) {
        musicSource.loadArtistAlbums(artist, callback)
    }

    override fun loadCollectByCategory(category: String, callback: LoadCollectByCategoryCallback) {
        musicSource.loadCollectByCategory(category, callback)
    }
}


