package com.example.wenhai.listenall.moudle.play

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.main.MainActivity
import com.example.wenhai.listenall.moudle.play.service.PlayService
import com.example.wenhai.listenall.moudle.play.service.PlayStatusObserver
import com.example.wenhai.listenall.utils.FragmentUtil
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

    lateinit var coverView: LinearLayout
    lateinit var mTvArtistName: TextView
    lateinit var mTvProvider: TextView
    lateinit var mIvCover: ImageView

    lateinit var lyricView: LinearLayout


    lateinit var mUnBinder: Unbinder
    var mCurrentSong: Song? = null
    lateinit var mCurrentPlayList: ArrayList<Song>
    lateinit var playService: PlayService
    var playMode: PlayService.PlayMode = PlayService.PlayMode.REPEAT_LIST
    var isPlaying: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playService = (activity as MainActivity).playService
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater !!.inflate(R.layout.fragment_play, container, false)
        //init coverView
        coverView = inflater.inflate(R.layout.fragment_play_cover, container, false) as LinearLayout
        mIvCover = coverView.findViewById(R.id.play_cover)
        mTvArtistName = coverView.findViewById(R.id.play_artist_name)
        mTvProvider = coverView.findViewById(R.id.play_provider)

        //init lyricView
        lyricView = inflater.inflate(R.layout.fragment_play_lyric, container, false) as LinearLayout
        //init
        mUnBinder = ButterKnife.bind(this, contentView)
        initView()
        playService.registerStatusObserver(this)
        return contentView
    }

    private fun initView() {
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

    }

    @OnClick(R.id.action_bar_back, R.id.play_btn_start_pause, R.id.play_btn_previous,
            R.id.play_btn_next, R.id.play_btn_mode, R.id.play_btn_song_list)
    fun onClick(view: View) {
        when (view.id) {
            R.id.action_bar_back -> {
                playService.unregisterStatusObserver(this)
                FragmentUtil.removeFragment(fragmentManager, this)
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
        if (hour == 0) {
            return "${formatStringNumber(minute)}:${formatStringNumber(second)}"
        } else {
            return "${formatStringNumber(hour)}:${formatStringNumber(minute)}:${formatStringNumber(second)}"
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

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
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
            mTvArtistName.text = mCurrentSong !!.artistName
            setProvider()
            setCover(mCurrentSong !!.albumCoverUrl)
            mTvTotalTime.text = getMinuteLength(mCurrentSong !!.length)
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

    }

    override fun onNewSong(song: Song) {
        mCurrentSong = song
        mSongName.text = mCurrentSong !!.name
        setCover(mCurrentSong !!.albumCoverUrl)
        mTvArtistName.text = mCurrentSong !!.artistName
        setProvider()
        setTotalTime(mCurrentSong !!.length)
        setCurTime(0f)
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun setProvider() {
        val provider = mCurrentSong !!.supplier
        val providerStr = when (provider) {
            MusicProvider.XIAMI -> {
                "虾米音乐"
            }
            MusicProvider.QQMUSIC -> {
                "QQ音乐"
            }
            MusicProvider.NETEASE -> {
                "网易云音乐"
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
            if (position == 0) {
                container !!.addView(coverView, 0)
                return coverView
            } else {
                container !!.addView(lyricView, 1)
                return lyricView
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