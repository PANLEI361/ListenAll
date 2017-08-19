package com.example.wenhai.listenall.data

import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.onlineprovider.Xiami
import com.example.wenhai.listenall.moudle.ranking.RankingContract

internal class MusicRepository() : MusicSource {

    private var musicSource: MusicSource
    private var curProvider: MusicProvider

    companion object {
        @JvmStatic
        val INSTANCE = MusicRepository()
    }

    init {
        //default
        curProvider = MusicProvider.XIAMI
        musicSource = Xiami()
    }

    constructor(sourceProvider: MusicProvider) : this() {
        curProvider = sourceProvider
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
        if (provider != curProvider) {
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

    override fun loadOfficialRanking(provider: MusicProvider, callback: LoadRankingCallback) {
        musicSource.loadOfficialRanking(provider, callback)
    }

    override fun loadGlobalRanking(ranking: RankingContract.GlobalRanking, callback: LoadSingleRankingCallback) {
        musicSource.loadGlobalRanking(ranking, callback)
    }

}


