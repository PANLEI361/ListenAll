package com.example.wenhai.listenall.moudle.search

import com.example.wenhai.listenall.data.LoadSearchRecommendCallback
import com.example.wenhai.listenall.data.LoadSearchResultCallback
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Song

internal class SearchPresenter(val view: SearchContract.View) : SearchContract.Presenter {
    private val musicRepository: MusicRepository = MusicRepository.INSTANCE

    init {
        view.setPresenter(this)
    }

    override fun searchByKeyWord(keyword: String) {
        musicRepository.searchByKeyword(keyword, object : LoadSearchResultCallback {
            override fun onStart() {
            }

            override fun onFailure(msg: String) {

            }

            override fun onSuccess(loadedSongs: List<Song>) {
                view.onSearchResult(loadedSongs)
            }

        })

    }

    override fun loadSearchRecommend(keyword: String) {
        musicRepository.loadSearchRecommend(keyword, object : LoadSearchRecommendCallback {
            override fun onStart() {
            }

            override fun onFailure(msg: String) {
            }

            override fun onSuccess(recommendKeyword: List<String>) {
                view.onSearchRecommendLoaded(recommendKeyword)
            }

        })
    }

    override fun loadSongDetail(song: Song) {
        musicRepository.loadSongDetail(song, object : LoadSongDetailCallback {
            override fun onStart() {
            }

            override fun onFailure(msg: String) {
                view.onLoadFailure("当前歌曲不能播放，请切换其他平台试试")
            }

            override fun onSuccess(loadedSong: Song) {
                view.onSongDetailLoad(loadedSong)
            }

        })
    }

}