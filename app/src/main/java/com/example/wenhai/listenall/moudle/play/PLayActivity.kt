package com.example.wenhai.listenall.moudle.play

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.FragmentUtil

class PLayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        var playFragment: PlayFragment? = supportFragmentManager.findFragmentById(R.id.play_container) as? PlayFragment
        if (playFragment == null) {
            playFragment = PlayFragment()
            FragmentUtil.addFragmentToActivity(supportFragmentManager, playFragment, R.id.play_container)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
