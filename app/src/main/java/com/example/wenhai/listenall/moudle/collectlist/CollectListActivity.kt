package com.example.wenhai.listenall.moudle.collectlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.ActivityUtil

class CollectListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_list)
        var collectListFragment: CollectListFragment? = supportFragmentManager.findFragmentById(R.id.collect_list_container) as? CollectListFragment
        if (collectListFragment == null) {
            collectListFragment = CollectListFragment()
            //create Presenter
            CollectListPresenter(collectListFragment)
            ActivityUtil.addFragmentToActivity(supportFragmentManager, collectListFragment, R.id.collect_list_container)
        }
    }
}
