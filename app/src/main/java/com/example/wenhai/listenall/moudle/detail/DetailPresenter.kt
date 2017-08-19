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


    companion object {
        const val TAG = "DetailPresenter"
    }

    private val musicRepository: MusicRepository = MusicRepository.INSTANCE

    init {
        view.setPresenter(this)
    }

    override fun loadSongsDetails(id: Long, type: DetailContract.LoadType) {
        if (type == DetailContract.LoadType.COLLECT) {
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
        } else {
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
    }

    override fun loadSongDetail(song: Song) {
        musicRepository.loadSongDetail(song, object : LoadSongDetailCallback {
            override fun onStart() {

            }

            override fun onFailure(msg: String) {
                view.onFailure(msg)
            }

            override fun onSuccess(loadedSong: Song) {
                view.onSongDetailLoad(loadedSong)
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

}