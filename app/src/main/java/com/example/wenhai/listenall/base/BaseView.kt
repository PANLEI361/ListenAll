package com.example.wenhai.listenall.base

interface BaseView<in T> {
    fun setPresenter(presenter: T)
    fun initView()
    fun onLoading()
    fun onFailure(msg: String)
}