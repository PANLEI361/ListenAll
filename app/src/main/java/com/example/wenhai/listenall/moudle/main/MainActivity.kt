package com.example.wenhai.listenall.moudle.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.ActivityUtil


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mainFragment: MainFragment? = supportFragmentManager.findFragmentById(R.id.container) as? MainFragment
        if (mainFragment == null) {
            mainFragment = MainFragment()
            ActivityUtil.addFragmentToActivity(supportFragmentManager, mainFragment, R.id.container)
        }
        //create presenter
    }

}
