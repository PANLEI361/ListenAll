package com.example.wenhai.listenall.module.collectlist

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Collect

interface CollectListContract {
    interface View : BaseView<Presenter> {
        fun setCollects(collects: List<Collect>)

    }

    interface Presenter : BasePresenter {
        fun loadCollects(page: Int)
    }
}