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
    }

    var mediaPlayer: MediaPlayer? = null
    val testUrl = "http://om5.alicdn.com/475/2110028475/2102806767/1796422395_1501586451944.mp3?auth_key=7c9f0eb3f1782c14801469d55ac5a7d8-1502766000-0-null"
    var isFirstStart = true
    val binder: Binder = ControlBinder()
    lateinit var currentListenUrl: String
    var mStatusObservers: ArrayList<PlayStatusObserver> = ArrayList()


    override fun onCreate() {
        super.onCreate()
        isFirstStart = true
        LogUtil.d(TAG, "play service created")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent !!.action == ACTION_INIT) {

        }
        isFirstStart = false
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        isFirstStart = false
        if (intent.action == ACTION_NEW_SONG) {
            val url = intent.getStringExtra("listenUrl")
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
            playNewSong(url)
        } else if (intent.action == ACTION_INIT) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
        }
        return binder
    }

    @Suppress("DEPRECATION")
    fun playNewSong(listenUrl: String) {
        if (isMediaPlaying()) {
            pause()
        }
        mediaPlayer !!.reset()
        mediaPlayer !!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer !!.setDataSource(listenUrl)
        } catch (e: IllegalArgumentException) {
            LogUtil.e(TAG, "参数有误")
        } catch (e: IOException) {
            LogUtil.e(TAG, "文件打开失败")
        }
        mediaPlayer !!.setOnErrorListener(this)
        mediaPlayer !!.setOnPreparedListener(this)
        mediaPlayer !!.setOnSeekCompleteListener(this)
        mediaPlayer !!.setOnCompletionListener(this)
        mediaPlayer !!.setOnBufferingUpdateListener(this)
        mediaPlayer !!.prepareAsync()
    }

    override fun onPrepared(player: MediaPlayer?) {
        start()
    }


    override fun onSeekComplete(player: MediaPlayer?) {
        //进度条定位完成
    }

    override fun onCompletion(player: MediaPlayer?) {
        // 播放完成
        LogUtil.d(TAG, "播放完成")
    }


    fun start() {
        if (mediaPlayer != null && ! mediaPlayer !!.isPlaying) {
            mediaPlayer !!.start()
            notifyStatusChanged(STATUS_START, null)
        }
    }

    fun pause() {
        if (mediaPlayer != null && mediaPlayer !!.isPlaying) {
            mediaPlayer !!.pause()
            notifyStatusChanged(STATUS_PAUSE, null)
        }
    }

    fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer !!.stop()
            notifyStatusChanged(STATUS_STOP, null)
        }
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
        LogUtil.d(TAG, "bufferpercent:$percent%")

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
        return ((mediaPlayer !!.currentPosition / mediaPlayer !!.duration.toDouble()) * 100).toInt()
    }

    fun seekTo(millionSec: Int) {
        mediaPlayer !!.seekTo(millionSec)
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
            }
        }

    }

    fun isMediaPlaying(): Boolean {
        if (mediaPlayer != null) {
            return mediaPlayer !!.isPlaying
        } else {
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(TAG, "play service destroyed")
        if (mediaPlayer != null) {
            mediaPlayer !!.release()
            mediaPlayer = null
            LogUtil.d(TAG, "player released")
        }
    }

    interface PlayStatusObserver {
        fun onPlayStart()
        fun onPlayPause()
        fun onPlayStop()

        //缓存进度更新
        fun onBufferProgressUpdate(percent: Int)

        //播放进度更新
        fun onPlayProgressUpdate(percent: Int)

        fun onPlayError(msg: String)

        fun onPlayInfo(msg: String)
    }

    inner class ControlBinder : Binder() {
        fun getPlayService(): PlayService {
            return this@PlayService
        }
    }
}
