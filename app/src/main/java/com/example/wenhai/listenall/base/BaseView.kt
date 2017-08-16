package com.example.wenhai.listenall.base

interface BaseView<in T> {
    fun setPresenter(presenter: T)
    fun initView()
    fun onFailure(msg: String)
}