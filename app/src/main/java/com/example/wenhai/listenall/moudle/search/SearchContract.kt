package com.example.wenhai.listenall.moudle.search

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Song

class SearchContract {

    interface View : BaseView<Presenter> {
        fun onSearchResult(songs: List<Song>)
        fun onSearchRecommendLoaded(recommends: List<String>)
    }

    interface Presenter : BasePresenter {

        fun searchByKeyWord(keyword: String)
        fun loadSearchRecommend(keyword: String)
    }
}