package com.example.wenhai.listenall.module.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.common.Config
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.module.artist.detail.ArtistDetailFragment
import com.example.wenhai.listenall.module.detail.DetailFragment
import com.example.wenhai.listenall.module.play.PLayActivity
import com.example.wenhai.listenall.module.play.service.PlayProxy
import com.example.wenhai.listenall.module.play.service.PlayService
import com.example.wenhai.listenall.module.play.service.PlayStatusObserver
import com.example.wenhai.listenall.utils.FragmentUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.LogUtil
import com.example.wenhai.listenall.utils.getAppVersionName
import com.example.wenhai.listenall.widget.PlayListDialog
import com.example.wenhai.listenall.widget.ProgressImageButton

class MainActivity : AppCompatActivity(), PlayStatusObserver, PlayProxy {
    //views in drawer
    @BindView(R.id.main_drawer)
    lateinit var mDrawer: DrawerLayout
    @BindView(R.id.slide_menu_app_version)
    lateinit var smTvAppVersion: TextView
    @BindView(R.id.slide_menu_cover)
    lateinit var smCover: ImageView
    @BindView(R.id.slide_menu_title)
    lateinit var smTitle: TextView
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

    // views in bottom play bar
    @BindView(R.id.main_iv_cover)
    lateinit var mCover: ImageView
    @BindView(R.id.main_song_name)
    lateinit var mSongName: TextView
    @BindView(R.id.main_singer_or_lyric)
    lateinit var mSingerOrLyric: TextView
    @BindView(R.id.play_bar_control)
    lateinit var mBtnControl: ProgressImageButton


    private var connection: ServiceConnection? = null
    lateinit var playService: PlayService
    private var isPlaying = false
    private var currentSong: Song? = null

    private lateinit var currentPlayList: ArrayList<Song>
    private var backKeyEventListeners: ArrayList<OnBackKeyEventListener>? = null

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
        initPlayService()
    }

    private fun initSlideMenu() {
        // TODO: 2017/8/4  初始化侧滑菜单
        val appVersion = getAppVersionName(this)
        val displayAppVersion = "V $appVersion"
        smTvAppVersion.text = displayAppVersion
        smTvAppVersion.isSelected = true

        //onlyWifi
        val sp = getSharedPreferences(Config.NAME, Context.MODE_PRIVATE)
        val onlyWifi = sp.getBoolean(Config.ONLY_WIFI, false)
        smSwitchOnlyWifi.isChecked = onlyWifi
        smSwitchOnlyWifi.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean(Config.ONLY_WIFI, isChecked).apply()
        }

    }

    private fun initPlayService() {
        val intent = Intent(this, PlayService::class.java)
        intent.action = PlayService.ACTION_INIT
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {

            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                val serviceBinder: PlayService.ServiceBinder = binder as PlayService.ServiceBinder
                playService = serviceBinder.getPlayService()
                playService.registerStatusObserver(this@MainActivity)
            }

        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
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

    @OnClick(R.id.play_bar_control, R.id.main_iv_cover, R.id.main_ll_song_info, R.id.play_bar_song_list)
    fun onPlayBarClick(view: View) {
        when (view.id) {
            R.id.play_bar_control -> {
                if (!isPlaying) {
                    playService.start()
                } else {
                    playService.pause()
                }
            }
            R.id.main_ll_song_info, R.id.main_iv_cover -> {
                startActivityForResult(Intent(this, PLayActivity::class.java), REQUSET_CODE)
            }
            R.id.play_bar_song_list -> {
                val dialog = PlayListDialog(this, currentPlayList)
                dialog.setOnItemClickListener(object : PlayListDialog.OnItemClickListener {
                    override fun onItemClick(song: Song) {
                        playService.playNewSong(song)
                        dialog.dismiss()
                    }

                })
                dialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUSET_CODE) {
            when (resultCode) {
                RESULT_SHOW_ALBUM -> {
                    showAlbumDetail(data)
                }
                RESULT_SHOW_ARTIST -> {
                    showArtistDetail(data)
                }
            }
        }
    }

    private fun showArtistDetail(data: Intent?) {
        val artistDetailFragment = ArtistDetailFragment()
        artistDetailFragment.arguments = data?.extras
        FragmentUtil.addFragmentToMainView(supportFragmentManager, artistDetailFragment)
    }

    private fun showAlbumDetail(data: Intent?) {
        val detailFragment = DetailFragment()
        detailFragment.arguments = data?.extras
        FragmentUtil.addFragmentToMainView(supportFragmentManager, detailFragment)
    }

    fun openDrawer() {
        mDrawer.openDrawer(Gravity.START)
    }

    override fun playSong(song: Song) {
        runOnUiThread {
            playService.playNewSong(song)
        }
    }

    /**
     * 下一首播放
     */
    override fun setNextSong(song: Song): Boolean = playService.setNextSong(song)

    fun addBackKeyEventListener(listener: OnBackKeyEventListener) {
        if (backKeyEventListeners == null) {
            backKeyEventListeners = ArrayList()
        }
        backKeyEventListeners?.add(listener)

    }

    fun removeBackKeyEventListener(listener: OnBackKeyEventListener) {
        backKeyEventListeners?.remove(listener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backKeyEventListeners != null && backKeyEventListeners!!.size > 0) {
                for (i in 0 until backKeyEventListeners!!.size) {
                    backKeyEventListeners!![i].onBackKeyPressed()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (connection != null) {
            playService.unregisterStatusObserver(this)
            unbindService(connection)
        }
    }

    //call from PlayService.PlayStatusObserver
    override fun onPlayInit(playStatus: PlayService.PlayStatus) {
        currentSong = playStatus.currentSong
        if (currentSong != null) {
            mSongName.text = currentSong!!.name
            mSingerOrLyric.text = currentSong!!.displayArtistName
            setCover(currentSong!!.miniAlbumCoverUrl)
        }
        isPlaying = playStatus.isPlaying
        setControlIcon(isPlaying)

        mBtnControl.progress = playStatus.playProgress
        currentPlayList = playStatus.currentList
    }

    private fun setCover(coverUrl: String) {
        GlideApp.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.ic_main_all_music)
                .into(mCover)
    }

    private fun setControlIcon(playing: Boolean) {
        val drawableId =
                if (playing) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play_arrow
                }
        mBtnControl.setDrawable(drawableId)
    }

    override fun onPlayStart() {
        runOnUiThread {
            isPlaying = true
            setControlIcon(isPlaying)
            //设置侧滑菜单顶部图标
            GlideApp.with(this)
                    .load(currentSong?.albumCoverUrl)
                    .into(smCover)
            smTitle.text = currentSong?.name
            smTvAppVersion.text = currentSong?.displayArtistName
        }
    }

    override fun onPlayPause() {
        runOnUiThread {
            isPlaying = false
            setControlIcon(isPlaying)
            //设置侧滑菜单顶部图标
            smCover.setImageResource(R.mipmap.ic_launcher)
            smTitle.text = getString(R.string.app_name)
            smTvAppVersion.text = getAppVersionName(this)
        }
    }

    override fun onPlayStop() {
        onPlayPause()
    }

    override fun onPlayModeChanged(playMode: PlayService.PlayMode) {

    }

    override fun onSongCompleted() {

    }

    override fun onBufferProgressUpdate(percent: Int) {

    }

    override fun onPlayProgressUpdate(percent: Float) {
        runOnUiThread {
            mBtnControl.animateProgress(percent)
        }
    }

    override fun onPlayError(msg: String) {
        LogUtil.e(TAG, msg)
    }

    override fun onPlayInfo(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewSong(song: Song) {
        runOnUiThread {
            currentSong = song
            mSongName.text = song.name
            mSingerOrLyric.text = song.displayArtistName
            mBtnControl.animateProgress(0.toFloat())
            setCover(song.miniAlbumCoverUrl)
        }
    }

    override fun onNewSongList() {
        mSongName.text = ""
        mSingerOrLyric.text = ""
        mBtnControl.animateProgress(0.toFloat())
        mCover.setImageResource(R.drawable.ic_main_all_music)
    }

    companion object {
        const val TAG = "MainActivity"
        const val REQUSET_CODE = 0x01
        const val RESULT_SHOW_ALBUM = 0x11
        const val RESULT_SHOW_ARTIST = 0x12
    }


    interface OnBackKeyEventListener {
        fun onBackKeyPressed()
    }
}
