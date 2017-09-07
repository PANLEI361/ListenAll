package com.example.wenhai.listenall.moudle.play

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
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
import com.example.wenhai.listenall.ktextension.showToast
import com.example.wenhai.listenall.moudle.play.service.PlayService
import com.example.wenhai.listenall.moudle.play.service.PlayStatusObserver
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.GlideApp
import com.example.wenhai.listenall.widget.PlayListDialog

class PlayFragment : Fragment(), PlayStatusObserver {
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
    @BindView(R.id.play_btn_previous)
    lateinit var mBtnPrevious: ImageButton
    @BindView(R.id.play_btn_start_pause)
    lateinit var mBtnPause: ImageButton
    @BindView(R.id.play_btn_next)
    lateinit var mBtnNext: ImageButton
    @BindView(R.id.play_btn_song_list)
    lateinit var mBtnSongList: ImageButton

    lateinit var coverView: RelativeLayout
    private lateinit var mTvArtistName: TextView
    private lateinit var mTvProvider: TextView
    private lateinit var mIvCover: ImageView
    private lateinit var mBtnLiked: ImageView
    private lateinit var mBtnDownload: ImageView
    private lateinit var mBtnMore: ImageView

    lateinit var lyricView: LinearLayout

    private lateinit var connection: ServiceConnection
    private lateinit var mUnBinder: Unbinder
    private var mCurrentSong: Song? = null
    private lateinit var mCurrentPlayList: ArrayList<Song>
    lateinit var playService: PlayService
    private var playMode: PlayService.PlayMode = PlayService.PlayMode.REPEAT_LIST
    private var isPlaying: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_play, container, false)
        //init coverView
        coverView = inflater.inflate(R.layout.fragment_play_cover, container, false) as RelativeLayout

        //init lyricView
        lyricView = inflater.inflate(R.layout.fragment_play_lyric, container, false) as LinearLayout
        //init
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        initPlayService()
        return contentView
    }

    //bind PlayService
    private fun initPlayService() {
        val intent = Intent(context, PlayService::class.java)
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {

            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                val serviceBinder: PlayService.ServiceBinder = binder as PlayService.ServiceBinder
                playService = serviceBinder.getPlayService()
                playService.registerStatusObserver(this@PlayFragment)
            }

        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }


    private fun initView() {
        mSongName.isSelected = true //validate marquee
        mPager.adapter = PlayPagerAdapter()
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

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
                playService.seekTo(seekBar !!.progress.toFloat())
            }

        })

        initCoverView()

    }

    private fun initCoverView() {
        mIvCover = coverView.findViewById(R.id.play_cover)
        mTvArtistName = coverView.findViewById(R.id.play_artist_name)
        //TextView 跑马灯需要设置 selected=true
        mTvArtistName.isSelected = true
        mTvProvider = coverView.findViewById(R.id.play_provider)
        mBtnLiked = coverView.findViewById(R.id.play_btn_like)
        mBtnDownload = coverView.findViewById(R.id.play_btn_download)
        mBtnMore = coverView.findViewById(R.id.play_btn_more_operation)
        mBtnLiked.setOnClickListener {
            likeCurrentSong()
        }

    }

    @OnClick(R.id.action_bar_back, R.id.play_btn_start_pause, R.id.play_btn_previous,
            R.id.play_btn_next, R.id.play_btn_mode, R.id.play_btn_song_list)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                activity.finish()
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
                val dialog = PlayListDialog(context, mCurrentPlayList)
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

    private fun likeCurrentSong() {
        val likedSongDao = DAOUtil.getSession(context).likedSongDao
        val queryList = likedSongDao.queryBuilder()
                .where(LikedSongDao.Properties.SongId.eq(mCurrentSong !!.songId))
                .list()
        if (queryList.isEmpty()) {
            //添加当前歌曲到喜欢列表
            val likedSong = LikedSong(mCurrentSong)
            if (likedSongDao.insert(likedSong) > 0) {
                context.showToast(R.string.liked)
                mBtnLiked.setImageResource(R.drawable.ic_liked)
            }
        } else {
            //将当前歌曲从喜欢列表中移除
            val likedSong = queryList[0]
            likedSongDao.delete(likedSong)
            mBtnLiked.setImageResource(R.drawable.ic_like_border)
            context.showToast(R.string.unliked)
        }
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
            mTvCurTime.text = getMinuteLength((progress / 100 * mCurrentSong !!.length).toInt())
        }
    }

    private fun setTotalTime(length: Int) {
        mTvTotalTime.text = getMinuteLength(length)
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
            mSongName.text = mCurrentSong !!.name
            mTvArtistName.text = mCurrentSong !!.displayArtistName
            setProvider()
            setCover(mCurrentSong !!.albumCoverUrl)
            mTvTotalTime.text = getMinuteLength(mCurrentSong !!.length)
            val isLiked = DAOUtil.getSession(context).likedSongDao.queryBuilder()
                    .where(LikedSongDao.Properties.SongId.eq(mCurrentSong !!.songId))
                    .list().size > 0
            if (isLiked) {
                mBtnLiked.setImageResource(R.drawable.ic_liked)
            }
        }
        mCurrentPlayList = playStatus.currentList
        mSeekBar.progress = playStatus.playProgress.toInt()
        setCurTime(playStatus.playProgress)
    }

    override fun onPlayStart() {
        isPlaying = true
        setPlayControlIcon(isPlaying)
    }

    override fun onPlayPause() {
        isPlaying = false
        setPlayControlIcon(isPlaying)
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
        activity.runOnUiThread {
            mSeekBar.progress = percent.toInt()
            setCurTime(percent)
        }
    }

    override fun onPlayError(msg: String) {

    }

    override fun onPlayInfo(msg: String) {
        context.showToast(msg)
    }

    override fun onNewSong(song: Song) {
        activity.runOnUiThread {
            mCurrentSong = song
            mSongName.text = mCurrentSong !!.name
            setCover(mCurrentSong !!.albumCoverUrl)
            mTvArtistName.text = mCurrentSong !!.displayArtistName
            setProvider()
            setTotalTime(mCurrentSong !!.length)
            setCurTime(0f)
            val isLiked = DAOUtil.getSession(context).likedSongDao.queryBuilder()
                    .where(LikedSongDao.Properties.SongId.eq(mCurrentSong !!.songId))
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
        val provider = mCurrentSong !!.supplier
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

    override fun onDestroyView() {
        playService.unregisterStatusObserver(this)
        super.onDestroyView()
        mUnBinder.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
        context.unbindService(connection)
    }

    inner class PlayPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            return if (position == 0) {
                container !!.addView(coverView, 0)
                coverView
            } else {
                container !!.addView(lyricView, 1)
                lyricView
            }
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

        override fun getCount(): Int = 2

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container !!.removeViewAt(position)
            super.destroyItem(container, position, `object`)
        }
    }
}