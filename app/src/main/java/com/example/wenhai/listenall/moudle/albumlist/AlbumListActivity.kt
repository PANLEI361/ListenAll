package com.example.wenhai.listenall.moudle.albumlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.utils.ActivityUtil

class AlbumListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)
        var albumListFragment: AlbumListFragment? = supportFragmentManager.findFragmentById(R.id.album_list_container) as? AlbumListFragment
        if (albumListFragment == null) {
            albumListFragment = AlbumListFragment()
            AlbumListPresenter(albumListFragment)
            ActivityUtil.addFragmentToActivity(supportFragmentManager, albumListFragment, R.id.album_list_container)
        }
    }
}
