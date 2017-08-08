package com.example.wenhai.listenall.moudle.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.service.PlayService
import com.example.wenhai.listenall.utils.AppUtil
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.LogUtil


class MainActivity : AppCompatActivity(), PlayService.PlayStatusObserver {


    companion object {
        const val TAG = "MainActivity"
    }

    //views of slide menu
    @BindView(R.id.slide_menu_app_version)
    lateinit var smTvAppVersion: TextView
    @BindView(R.id.slide_only_wifi_switcher)
    lateinit var smSwitchOnlyWifi: Switch
    @BindView(R.id.slide_set_time_close_switcher)
    lateinit var smSwitchSetCloseTime: Switch
    @BindView(R.id.slide_menu_clean_cache)
    lateinit var smLlCleanCache: LinearLayout
    @BindView(R.id.slide_menu_tv_used_cache)
    lateinit var smTvUsedCache: TextView
    @BindView(R.id.slide_menu_set_cache_size)
    lateinit var smLlSetCacheSize: LinearLayout
    @BindView(R.id.slide_menu_tv_allowed_cache)
    lateinit var smTvAllowedCache: TextView
    @BindView(R.id.slide_menu_open_source)
    lateinit var smLlOpenSource: LinearLayout
    @BindView(R.id.slide_menu_about_app)
    lateinit var smLlAboutApp: LinearLayout
    @BindView(R.id.slide_menu_quit)
    lateinit var smBtnQuit: Button

    @BindView(R.id.main_drawer)
    lateinit var mDrawer: DrawerLayout
    @BindView(R.id.play_bar_control)
    lateinit var mBtnControl: ImageButton

    var connection: ServiceConnection? = null
    lateinit var playService: PlayService
    var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        var mainFragment: MainFragment? = supportFragmentManager.findFragmentById(R.id.main_container) as? MainFragment
        if (mainFragment == null) {
            mainFragment = MainFragment()
            FragmentUtil.addFragmentToActivity(supportFragmentManager, mainFragment, R.id.main_container)
        }
        initSlideMenu()
        bindPlayService()
    }


    @OnClick(R.id.slide_only_wifi_switcher, R.id.slide_set_time_close_switcher, R.id.slide_menu_clean_cache,
            R.id.slide_menu_set_cache_size, R.id.slide_menu_open_source, R.id.slide_menu_about_app,
            R.id.slide_menu_quit)
    fun onSlideMenuItemClick(v: View) {
        when (v.id) {
            R.id.slide_menu_quit -> {
                finish()
            }
        }
    }

    @OnClick(R.id.play_bar_control)
    fun onPlayBarClick(view: View) {
        when (view.id) {
            R.id.play_bar_control -> {
                if (! isPlaying) {
                    playService.start()
                } else {
                    playService.pause()
                }
            }
        }
    }

    private fun bindPlayService() {
        val intent = Intent(this, PlayService::class.java)
        intent.action = PlayService.ACTION_INIT
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {

            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                val controlBinder: PlayService.ControlBinder = binder as PlayService.ControlBinder
                playService = controlBinder.getPlayService()
                playService.registerStatusObserver(this@MainActivity)
            }

        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun initSlideMenu() {
        // TODO: 2017/8/4  初始化侧滑菜单
        val appVersion = AppUtil.getAppVersionName(this)
        val displayAppVersion = "V $appVersion"
        smTvAppVersion.text = displayAppVersion
    }

    fun openDrawer() {
        mDrawer.openDrawer(Gravity.START)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (connection != null) {
            unbindService(connection)
        }
    }

    //PlayService.PlayStatusObserver 的回调
    override fun onPlayStart() {
        isPlaying = true
        mBtnControl.setImageResource(R.drawable.ic_main_pause)
//        ToastUtil.showToast(this, "onPlayStart")
    }

    override fun onPlayPause() {
        isPlaying = false
        mBtnControl.setImageResource(R.drawable.ic_main_play)
//        ToastUtil.showToast(this, "onPlayPause")
    }

    override fun onPlayStop() {
        isPlaying = false
        mBtnControl.setImageResource(R.drawable.ic_main_play)
    }

    override fun onBufferProgressUpdate(percent: Int) {

    }

    override fun onPlayProgressUpdate(percent: Int) {

    }

    override fun onPlayError(msg: String) {
        LogUtil.e(TAG, msg)
    }

    override fun onPlayInfo(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onNewSong(song: Song) {

    }

}
