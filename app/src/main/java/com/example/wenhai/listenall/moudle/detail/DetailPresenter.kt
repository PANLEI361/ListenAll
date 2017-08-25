package com.example.wenhai.listenall.moudle.detail

import com.example.wenhai.listenall.data.LoadAlbumDetailCallback
import com.example.wenhai.listenall.data.LoadCollectDetailCallback
import com.example.wenhai.listenall.data.LoadSingleRankingCallback
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.ranking.RankingContract

internal class DetailPresenter(val view: DetailContract.View) : DetailContract.Presenter {

    private val musicRepository: MusicRepository = MusicRepository.INSTANCE

    init {
        view.setPresenter(this)
    }

    override fun loadAlbumDetail(id: Long) {
        musicRepository.loadAlbumDetail(id, object : LoadAlbumDetailCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(album: Album) {
                view.onAlbumDetailLoad(album)
            }

        })
    }

    override fun loadCollectDetail(id: Long) {
        musicRepository.loadCollectDetail(id, object : LoadCollectDetailCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(collect: Collect) {
                view.onCollectDetailLoad(collect)
            }

        })

    }

    override fun loadSongDetail(id: Long) {
        val song = Song()
        song.songId = id
        musicRepository.loadSongDetail(song, object : LoadSongDetailCallback {
            override fun onStart() {
                view.onLoading()
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(loadedSong: Song) {
                //加载专辑详情
                loadAlbumDetail(song.albumId)
            }

        })
    }


    override fun loadGlobalRanking(ranking: RankingContract.GlobalRanking) {
        musicRepository.loadGlobalRanking(ranking, object : LoadSingleRankingCallback {
            override fun onStart() {
                view.onLoading()

            }

            override fun onSuccess(collect: Collect) {
                view.onGlobalRankingLoad(collect)
            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }
        })

    }

    companion object {
        const val TAG = "DetailPresenter"
    }

}