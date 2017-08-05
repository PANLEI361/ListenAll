package com.example.wenhai.listenall.moudle.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.ActivityUtil

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        var detailFragment: DetailFragment? = supportFragmentManager.findFragmentById(R.id.detail_container) as? DetailFragment
        if (detailFragment == null) {
            detailFragment = DetailFragment()
            detailFragment.arguments = intent.extras
            DetailPresenter(detailFragment)
            ActivityUtil.addFragmentToActivity(supportFragmentManager, detailFragment, R.id.detail_container)
        }
    }
}
