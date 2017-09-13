package com.example.wenhai.listenall.data

import android.content.Context
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.data.onlineprovider.Xiami
import com.example.wenhai.listenall.moudle.ranking.RankingContract

internal class MusicRepository(context: Context) : MusicSource {

    private var musicSource: MusicSource
    private var curProvider: MusicProvider

    companion object {
        @JvmStatic
        var INSTANCE: MusicRepository? = null

        @JvmStatic
        fun getInstance(context: Context): MusicRepository {
            if (INSTANCE == null) {
                synchronized(MusicRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MusicRepository(context)
                    }
                }
            }
            return INSTANCE !!
        }
    }

    init {
        //default
        curProvider = MusicProvider.XIAMI
        musicSource = Xiami(context)
    }

    constructor(sourceProvider: MusicProvider, context: Context) : this(context) {
        curProvider = sourceProvider
        when (sourceProvider) {
            MusicProvider.XIAMI -> {
                musicSource = Xiami(context)
            }
            MusicProvider.QQMUSIC -> {
//                musicSource = QQ()
            }
            MusicProvider.NETEASE -> {
//                musicSource = NetEase()
            }
        }
    }

    fun changeMusicSource(provider: MusicProvider, context: Context) {
        if (provider != curProvider) {
            when (provider) {
                MusicProvider.XIAMI -> {
                    musicSource = Xiami(context)
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

    override fun loadHotCollect(page: Int, callback: LoadCollectCallback) {
        musicSource.loadHotCollect(page, callback)
    }

    override fun loadNewAlbum(page: Int, callback: LoadAlbumCallback) {
        musicSource.loadNewAlbum(page, callback)
    }

    override fun loadArtists(region: ArtistRegion, page: Int, callback: LoadArtistsCallback) {
        musicSource.loadArtists(region, page, callback)
    }

    override fun loadArtistDetail(artist: Artist, callback: LoadArtistDetailCallback) {
        musicSource.loadArtistDetail(artist, callback)
    }

    override fun loadArtistHotSongs(artist: Artist, page: Int, callback: LoadArtistHotSongsCallback) {
        musicSource.loadArtistHotSongs(artist, page, callback)
    }

    override fun loadArtistAlbums(artist: Artist, page: Int, callback: LoadArtistAlbumsCallback) {
        musicSource.loadArtistAlbums(artist, page, callback)
    }

    override fun loadCollectByCategory(category: String, page: Int, callback: LoadCollectByCategoryCallback) {
        musicSource.loadCollectByCategory(category, page, callback)
    }

    override fun loadOfficialRanking(provider: MusicProvider, callback: LoadRankingCallback) {
        musicSource.loadOfficialRanking(provider, callback)
    }

    override fun loadGlobalRanking(ranking: RankingContract.GlobalRanking, callback: LoadSingleRankingCallback) {
        musicSource.loadGlobalRanking(ranking, callback)
    }

}


