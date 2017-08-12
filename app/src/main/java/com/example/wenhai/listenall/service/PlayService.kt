package com.example.wenhai.listenall.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.utils.LogUtil
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class PlayService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener {

    // TODO: 2017/8/8 添加到下一首播放可通过 setNextMediaPlayer 实现

    companion object {
        const val TAG = "PlayService"
        const val ACTION_NEW_SONG = "com.example.wenhai.listenall.action.newsong"
        const val ACTION_INIT = "com.example.wenhai.listenall.action.init"

        const val STATUS_STOP = 0x00
        const val STATUS_START = 0x01
        const val STATUS_PAUSE = 0x02
        const val STATUS_BUFFER_PROCESS_UPDATE = 0x03
        const val STATUS_PLAY_PROCESS_UPDATE = 0x04
        const val STATUS_ERROR = 0x05
        const val STATUS_INFO = 0x06
        const val STATUS_NEW_SONG = 0x07
        const val STATUS_INIT = 0x08
        const val STATUS_SONG_COMPLETED = 0x09
    }

    lateinit var mediaPlayer: MediaPlayer
    //    val testUrl = "http://om5.alicdn.com/475/2110028475/2102806767/1796422395_1501586451944.mp3?auth_key=7c9f0eb3f1782c14801469d55ac5a7d8-1502766000-0-null"
    val binder: Binder = ControlBinder()
    // TODO: 2017/8/8 退出时记录上次播放的歌和播放进度，第一次启动时恢复

    var currentSong: Song? = null
    lateinit var currentPlayList: ArrayList<Song>
    var mStatusObservers: ArrayList<PlayStatusObserver> = ArrayList()
    lateinit var timer: Timer
    var updateProgressTask: TimerTask? = null


    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG, "play service created")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent !!.action == ACTION_INIT) {

        }
        if (intent.action == ACTION_NEW_SONG) {
            val song = intent.getParcelableExtra<Song>("song")
            playNewSong(song)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        if (intent.action == ACTION_NEW_SONG) {
            val song = intent.getParcelableExtra<Song>("song")
            playNewSong(song)
        } else if (intent.action == ACTION_INIT) {
            mediaPlayer = MediaPlayer()
            timer = Timer()
        }
        return binder
    }

    @Suppress("DEPRECATION")
    fun playNewSong(song: Song) {
        if (isMediaPlaying()) {
            pause()
        }
        mediaPlayer.reset()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(song.listenFileUrl)
        } catch (e: IllegalArgumentException) {
            notifyStatusChanged(STATUS_ERROR, "参数有误")
            LogUtil.e(TAG, "参数有误")
        } catch (e: IOException) {
            notifyStatusChanged(STATUS_ERROR, "文件打开失败")
            LogUtil.e(TAG, "文件打开失败")
        }
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnBufferingUpdateListener(this)
        mediaPlayer.prepareAsync()
        currentSong = song
        notifyStatusChanged(STATUS_NEW_SONG, song)
    }

    override fun onPrepared(player: MediaPlayer?) {
        start()
    }


    override fun onSeekComplete(player: MediaPlayer?) {
        //进度条定位完成
    }

    override fun onCompletion(player: MediaPlayer?) {
        // 播放完成
        notifyStatusChanged(STATUS_SONG_COMPLETED, null)
        updateProgressTask !!.cancel()
        LogUtil.d(TAG, "播放完成")
    }


    fun start() {
        if (currentSong == null) {
            notifyStatusChanged(STATUS_INFO, "当前没有歌曲播放")
        } else {
            if (! mediaPlayer.isPlaying) {
                mediaPlayer.start()
                notifyStatusChanged(STATUS_START, null)
                updateProgressTask = ProgressTimerTask()
                timer.schedule(updateProgressTask, 0, 1000)
            }
        }

    }

    fun pause() {
        if (isMediaPlaying()) {
            mediaPlayer.pause()
            notifyStatusChanged(STATUS_PAUSE, null)
            updateProgressTask !!.cancel()
        }
    }

    fun stop() {
        mediaPlayer.stop()
        notifyStatusChanged(STATUS_STOP, null)
        updateProgressTask !!.cancel()

    }

    override fun onInfo(player: MediaPlayer?, what: Int, extra: Int): Boolean {
        val msg = when (what) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                "开始缓存"
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                "缓存结束"
            }
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {
                "文件错位"
            }
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                "文件不支持进度条定位"
            }
            MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING -> {
                "暂停"
            }
            else -> {
                ""
            }
        }
        notifyStatusChanged(STATUS_INFO, msg)
        return true
    }

    override fun onBufferingUpdate(player: MediaPlayer?, percent: Int) {
        notifyStatusChanged(STATUS_BUFFER_PROCESS_UPDATE, percent)
        LogUtil.d(TAG, "buffer percent:$percent%")

    }

    override fun onError(player: MediaPlayer?, what: Int, extra: Int): Boolean {
        var msg = ""
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                msg = "未知资源"
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                msg = "与服务器连接失败"
            }
        }
        when (extra) {
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                msg = "文件格式不支持"

            }
            MediaPlayer.MEDIA_ERROR_IO -> {
                msg = "打开失败"

            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                msg = "连接超时"
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                msg = "文件不符合编码格式"

            }
        }
        notifyStatusChanged(STATUS_ERROR, msg)
        return true
    }


    fun next() {

    }

    fun previous() {

    }

    fun addToPlayList(songs: List<Song>) {

    }

    fun setNextSong(song: Song) {

    }

    fun getCurrentPlayProgress(): Int {
        return ((mediaPlayer.currentPosition / mediaPlayer.duration.toDouble()) * 100).toInt()
    }

    fun seekTo(millionSec: Int) {
        mediaPlayer.seekTo(millionSec)
    }

    fun registerStatusObserver(observer: PlayStatusObserver) {
        mStatusObservers.add(observer)
    }

    fun unregisterStatusObserver(observer: PlayStatusObserver) {
        mStatusObservers.remove(observer)
    }

    fun notifyStatusChanged(status: Int, extra: Any?) {
        for (observer in mStatusObservers) {
            when (status) {
                STATUS_START -> observer.onPlayStart()
                STATUS_PAUSE -> observer.onPlayPause()
                STATUS_STOP -> observer.onPlayStop()
                STATUS_BUFFER_PROCESS_UPDATE -> observer.onBufferProgressUpdate(extra as Int)
                STATUS_PLAY_PROCESS_UPDATE -> observer.onPlayProgressUpdate(extra as Int)
                STATUS_ERROR -> observer.onPlayError(extra as String)
                STATUS_INFO -> observer.onPlayInfo(extra as String)
                STATUS_NEW_SONG -> observer.onNewSong(extra as Song)
                STATUS_SONG_COMPLETED -> observer.onSongCompleted()
            }
        }

    }

    fun isMediaPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        timer.cancel()
        LogUtil.d(TAG, "player released")
        LogUtil.d(TAG, "play service destroyed")

    }

    interface PlayStatusObserver {
        fun onPlayInit(song: Song)

        fun onPlayStart()

        fun onPlayPause()

        fun onPlayStop()

        //缓存进度更新
        fun onBufferProgressUpdate(percent: Int)

        //播放进度更新
        fun onPlayProgressUpdate(percent: Int)

        fun onPlayError(msg: String)

        fun onPlayInfo(msg: String)

        fun onNewSong(song: Song)

        fun onSongCompleted()
    }

    inner class ProgressTimerTask : TimerTask() {
        override fun run() {
            val progress = getCurrentPlayProgress()
            notifyStatusChanged(STATUS_PLAY_PROCESS_UPDATE, progress)
        }

    }

    inner class ControlBinder : Binder() {
        fun getPlayService(): PlayService {
            return this@PlayService
        }
    }
}
