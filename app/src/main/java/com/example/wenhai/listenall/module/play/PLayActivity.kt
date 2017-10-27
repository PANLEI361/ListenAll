package com.example.wenhai.listenall.module.play

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.LikedSong
import com.example.wenhai.listenall.data.bean.LikedSongDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.extension.showToast
import com.example.wenhai.listenall.module.play.service.PlayProxy
import com.example.wenhai.listenall.module.play.service.PlayService
import com.example.wenhai.listenall.module.play.service.PlayStatusObserver
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.utils.LogUtil
import com.example.wenhai.listenall.widget.PlayListDialog
import com.example.wenhai.listenall.widget.SongOpsDialog
import com.wenhaiz.lyricview.LyricView
import com.wenhaiz.lyricview.bean.Lyric
import com.wenhaiz.lyricview.utils.LyricUtil
import java.net.URL

class PLayActivity : AppCompatActivity(), PlayStatusObserver, PlayProxy {
    @BindView(R.id.play_song_name)
    lateinit var mSongName: TextView
    @BindView(R.id.play_pager)
    lateinit var mPager: ViewPager

    //    indicator
    @BindView(R.id.indicator_left)
    lateinit var ivLeftIndicator: ImageView
    @BindView(R.id.indicator_right)
    lateinit var ivRightIndicator: ImageView

    //    seek bar
    @BindView(R.id.play_tv_cur_time)
    lateinit var mTvCurTime: TextView
    @BindView(R.id.play_seek_bar)
    lateinit var mSeekBar: SeekBar
    @BindView(R.id.play_tv_total_time)
    lateinit var mTvTotalTime: TextView

    //    control bar
    @BindView(R.id.play_btn_mode)
    lateinit var mBtnPlayMode: ImageButton
    @BindView(R.id.play_btn_start_pause)
    lateinit var mBtnPause: ImageButton
//    @BindView(R.id.play_btn_previous)
//    lateinit var mBtnPrevious: ImageButton
//    @BindView(R.id.play_btn_next)
//    lateinit var mBtnNext: ImageButton
//    @BindView(R.id.play_btn_song_list)
//    lateinit var mBtnSongList: ImageButton

    lateinit var coverFragment: RelativeLayout
    private lateinit var mTvArtistName: TextView
    private lateinit var mTvProvider: TextView
    private lateinit var mIvCover: ImageView
    private lateinit var mBtnLiked: ImageView
    private lateinit var mBtnDownload: ImageView
    private lateinit var mBtnMore: ImageView

    lateinit var lyricFragment: LinearLayout
    lateinit var lyricView: LyricView

    private lateinit var connection: ServiceConnection
    private lateinit var mUnBinder: Unbinder
    private var mCurrentSong: Song? = null
    private lateinit var mCurrentPlayList: ArrayList<Song>
    lateinit var playService: PlayService
    private var playMode: PlayService.PlayMode = PlayService.PlayMode.REPEAT_LIST
    private var isPlaying: Boolean = false

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        //init coverFragment
        coverFragment = LayoutInflater.from(this).inflate(R.layout.fragment_play_cover, null, false) as RelativeLayout
        //init lyricFragment
        lyricFragment = LayoutInflater.from(this).inflate(R.layout.fragment_play_lyric, null, false) as LinearLayout
        mUnBinder = ButterKnife.bind(this)
        initView()
        initPlayService()
    }


    private fun initView() {
        mSongName.isSelected = true //validate marquee
        initViewPager()
        initSeekBar()
        initCoverView()
        initLyricView()
    }

    private fun initViewPager() {
        mPager.adapter = PlayPagerAdapter()
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    ivLeftIndicator.setImageResource(R.drawable.ic_indicator_selected)
                    ivRightIndicator.setImageResource(R.drawable.ic_indicator_normal)
                } else {
                    ivLeftIndicator.setImageResource(R.drawable.ic_indicator_normal)
                    ivRightIndicator.setImageResource(R.drawable.ic_indicator_selected)
                }
            }

        })
    }

    private fun initSeekBar() {
        mSeekBar.max = 100
        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    //显示时间跟随用户手指变化
                    setCurTime(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                playService.seekTo(seekBar!!.progress.toFloat())
            }

        })
    }

    private fun initCoverView() {
        mIvCover = coverFragment.findViewById(R.id.play_cover)
        mTvArtistName = coverFragment.findViewById(R.id.play_artist_name)
        //TextView 跑马灯需要设置 selected=true
        mTvArtistName.isSelected = true
        mTvProvider = coverFragment.findViewById(R.id.play_provider)
        mBtnLiked = coverFragment.findViewById(R.id.play_btn_like)
        mBtnDownload = coverFragment.findViewById(R.id.play_btn_download)
        mBtnMore = coverFragment.findViewById(R.id.play_btn_more_operation)
        mBtnLiked.setOnClickListener {
            likeCurrentSong()
        }

        mBtnMore.setOnClickListener {
            if (mCurrentSong != null) {
                val dialog = SongOpsDialog(this, mCurrentSong!!, this)
                dialog.canSaveCover = true
                dialog.showDelete = false
                dialog.show()
            } else {
                showToast("没有歌曲正在播放")
            }
        }

    }

    private fun likeCurrentSong() {
        val likedSongDao = DAOUtil.getSession(this).likedSongDao
        val queryList = likedSongDao.queryBuilder()
                .where(LikedSongDao.Properties.SongId.eq(mCurrentSong!!.songId))
                .list()
        if (queryList.isEmpty()) {
            //添加当前歌曲到喜欢列表
            val likedSong = LikedSong(mCurrentSong)
            if (likedSongDao.insert(likedSong) > 0) {
                showToast(R.string.liked)
                mBtnLiked.setImageResource(R.drawable.ic_liked)
            }
        } else {
            //将当前歌曲从喜欢列表中移除
            val likedSong = queryList[0]
            likedSongDao.delete(likedSong)
            mBtnLiked.setImageResource(R.drawable.ic_like_border)
            showToast(R.string.unliked)
        }
    }

    private fun initLyricView() {
        lyricView = lyricFragment.findViewById(R.id.lyric)
    }

    private fun parseLyric(lyricUrl: String?) {
        if (lyricUrl != null) {
            val thread = Thread {
                kotlin.run {
                    try {
                        val url = URL(lyricUrl)
                        val inputStream = url.openConnection().getInputStream()
                        val lyric = LyricUtil.parseLyric(inputStream, "utf-8")
                        onLyricLoaded(lyric)
                    } catch (e: Exception) {

                    }
                }
            }
            thread.start()
        } else {
            lyricView.lyric = null
        }
    }

    private fun onLyricLoaded(lyric: Lyric?) {
        runOnUiThread {
            lyricView.lyric = lyric
        }
    }

    //bind PlayService
    private fun initPlayService() {
        val intent = Intent(this, PlayService::class.java)
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {

            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                val serviceBinder: PlayService.ServiceBinder = binder as PlayService.ServiceBinder
                playService = serviceBinder.getPlayService()
                playService.registerStatusObserver(this@PLayActivity)
            }

        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }


    private fun setCover(url: String) {
        GlideApp.with(this)
                .load(url)
                .placeholder(R.drawable.ic_main_all_music)
                .into(mIvCover)
    }

    private fun setPlayControlIcon(isPlaying: Boolean) {
        val drawableId = if (isPlaying) {
            R.drawable.ic_main_pause
        } else {
            R.drawable.ic_main_play
        }
        mBtnPause.setImageResource(drawableId)
    }

    private fun setPlayModeIcon(playMode: PlayService.PlayMode) {
        val drawableId = when (playMode) {
            PlayService.PlayMode.REPEAT_LIST -> R.drawable.ic_repeat_list
            PlayService.PlayMode.REPEAT_ONE -> R.drawable.ic_repeat_one
            PlayService.PlayMode.SHUFFLE -> R.drawable.ic_shuffle
        }
        mBtnPlayMode.setImageResource(drawableId)
    }

    private fun getMinuteLength(length: Int): String {
        val hour = length / 3600
        val minute = (length - hour * 3600) / 60
        val second = length % 60
        return if (hour == 0) {
            "${formatStringNumber(minute)}:${formatStringNumber(second)}"
        } else {
            "${formatStringNumber(hour)}:${formatStringNumber(minute)}:${formatStringNumber(second)}"
        }
    }

    private fun formatStringNumber(number: Int): String {
        return if (number < 10) {
            "0$number"
        } else {
            number.toString()
        }
    }

    private fun setCurTime(progress: Float) {
        if (mCurrentSong != null) {
            mTvCurTime.text = getMinuteLength((progress / 100 * mCurrentSong!!.length).toInt())
        }
    }

    private fun setTotalTime(length: Int) {
        mTvTotalTime.text = getMinuteLength(length)
    }


    @OnClick(R.id.action_bar_back, R.id.play_btn_start_pause, R.id.play_btn_previous,
            R.id.play_btn_next, R.id.play_btn_mode, R.id.play_btn_song_list)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                finish()
            }
            R.id.play_btn_start_pause -> {
                if (playService.isMediaPlaying()) {
                    playService.pause()
                } else {
                    playService.start()
                }

            }
            R.id.play_btn_previous -> {
                playService.previous()
            }
            R.id.play_btn_next -> {
                playService.next()
            }
            R.id.play_btn_mode -> {
                playService.changePlayMode()
            }
            R.id.play_btn_song_list -> {
                val dialog = PlayListDialog(this, mCurrentPlayList)
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

    override fun onDestroy() {
        super.onDestroy()
        mUnBinder.unbind()
        playService.unregisterStatusObserver(this)
        unbindService(connection)
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun setNextSong(song: Song): Boolean = playService.setNextSong(song)

    override fun playSong(song: Song) {
        playService.playNewSong(song)
    }


    // call from PlayService
    override fun onPlayInit(playStatus: PlayService.PlayStatus) {
        playMode = playStatus.playMode
        setPlayModeIcon(playMode)
        isPlaying = playStatus.isPlaying
        setPlayControlIcon(isPlaying)
        mSeekBar.secondaryProgress = playStatus.bufferedProgress
        mCurrentSong = playStatus.currentSong
        if (mCurrentSong != null) {
            mSongName.text = mCurrentSong!!.name
            mTvArtistName.text = mCurrentSong!!.displayArtistName
            setProvider()
            setCover(mCurrentSong!!.albumCoverUrl)
            mTvTotalTime.text = getMinuteLength(mCurrentSong!!.length)
            val isLiked = DAOUtil.getSession(this).likedSongDao.queryBuilder()
                    .where(LikedSongDao.Properties.SongId.eq(mCurrentSong!!.songId))
                    .list().size > 0
            if (isLiked) {
                mBtnLiked.setImageResource(R.drawable.ic_liked)
            }
            if (!TextUtils.isEmpty(mCurrentSong!!.lyricUrl)) {
                parseLyric(mCurrentSong!!.lyricUrl)
            }
        }
        mCurrentPlayList = playStatus.currentList
        onPlayProgressUpdate(playStatus.playProgress)
    }

    override fun onPlayStart() {
        runOnUiThread {
            isPlaying = true
            setPlayControlIcon(isPlaying)
        }
    }

    override fun onPlayPause() {
        runOnUiThread {
            isPlaying = false
            setPlayControlIcon(isPlaying)
        }
    }

    override fun onPlayStop() {
        onPlayPause()
    }

    override fun onPlayModeChanged(playMode: PlayService.PlayMode) {
        setPlayModeIcon(playMode)
    }

    override fun onBufferProgressUpdate(percent: Int) {
        mSeekBar.secondaryProgress = percent
    }

    override fun onPlayProgressUpdate(percent: Float) {
        runOnUiThread {
            try {
                mSeekBar.progress = percent.toInt()
                setCurTime(percent)
                if (lyricView.hasLyric()) {
                    val timeInMills = (percent * mCurrentSong!!.length * 10).toLong()
                    lyricView.updateTime(timeInMills)
                }
            } catch (e: Exception) {
                LogUtil.e("playException", e.localizedMessage)
            }
        }
    }

    override fun onPlayError(msg: String) {

    }

    override fun onPlayInfo(msg: String) {
        showToast(msg)
    }

    override fun onNewSong(song: Song) {
        runOnUiThread {
            mCurrentSong = song
            parseLyric(mCurrentSong?.lyricUrl)
            mSongName.text = mCurrentSong!!.name
            setCover(mCurrentSong!!.albumCoverUrl)
            mTvArtistName.text = mCurrentSong!!.displayArtistName
            setProvider()
            setTotalTime(mCurrentSong!!.length)
            setCurTime(0f)
            val isLiked = DAOUtil.getSession(this).likedSongDao.queryBuilder()
                    .where(LikedSongDao.Properties.SongId.eq(mCurrentSong!!.songId))
                    .list().size > 0
            if (isLiked) {
                mBtnLiked.setImageResource(R.drawable.ic_liked)
            } else {
                mBtnLiked.setImageResource(R.drawable.ic_like_border)
            }
        }
    }

    override fun onNewSongList() {

    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun setProvider() {
        val provider = mCurrentSong!!.supplier
        val providerStr = when (provider) {
            MusicProvider.XIAMI -> {
                getString(R.string.provider_xiami)
            }
            MusicProvider.QQMUSIC -> {
                getString(R.string.provider_qq)
            }
            MusicProvider.NETEASE -> {
                getString(R.string.provider_netease)
            }
        }
        mTvProvider.text = providerStr
    }

    override fun onSongCompleted() {
        //adjust
        onPlayProgressUpdate(100f)
    }


    inner class PlayPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            return if (position == 0) {
                container!!.addView(coverFragment, 0)
                coverFragment
            } else {
                container!!.addView(lyricFragment, 1)
                lyricFragment
            }
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

        override fun getCount(): Int = 2

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container!!.removeViewAt(position)
            super.destroyItem(container, position, `object`)
        }
    }
}
