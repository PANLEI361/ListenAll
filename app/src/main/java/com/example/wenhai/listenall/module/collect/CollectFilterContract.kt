package com.example.wenhai.listenall.module.collect

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Collect


interface CollectFilterContract {
    interface View : BaseView<Presenter> {
        fun onCollectLoad(collects: List<Collect>)
    }

    interface Presenter : BasePresenter {
        fun loadCollectByCategory(category: String, page: Int)
    }
}