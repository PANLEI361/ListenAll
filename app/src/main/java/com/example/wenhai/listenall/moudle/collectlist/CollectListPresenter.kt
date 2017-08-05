package com.example.wenhai.listenall.moudle.collectlist

import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.utils.LogUtil

internal class CollectListPresenter(val view: CollectListContract.View) : CollectListContract.Presenter {
    val musicRepository: MusicRepository = MusicRepository()

    init {
        view.setPresenter(this)
    }


    override fun loadCollects(count: Int) {
        musicRepository.loadHotCollect(count, object : LoadCollectCallback {
            override fun onFailure() {
                LogUtil.e(TAG, "load collects failed in CollectListPresenter")
            }

            override fun onSuccess(collectList: List<Collect>) {
                view.setCollects(collectList)
            }
        })
    }

    companion object {
        const val TAG = "CollectListPresenter"
    }
}