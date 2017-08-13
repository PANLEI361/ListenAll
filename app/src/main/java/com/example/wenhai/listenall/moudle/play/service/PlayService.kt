package com.example.wenhai.listenall.moudle.play.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.wenhai.listenall.data.bean.PlayHistory
import com.example.wenhai.listenall.data.bean.PlayHistoryDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.LogUtil
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Timer
import java.util.TimerTask

class PlayService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener {

    companion object {
        const val TAG = "PlayService"
        const val STATUS_TMP_FILE_NAME = "play_status.tmp"
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
        const val STATUS_PLAY_MODE_CHANGED = 0x0a
    }

    enum class PlayMode : Serializable {
        REPEAT_ONE, REPEAT_LIST, SHUFFLE
    }

    lateinit var mediaPlayer: MediaPlayer
    val binder: Binder = ControlBinder()
    lateinit var mStatusObservers: ArrayList<PlayStatusObserver>
    lateinit var timer: Timer

    var updateProgressTask: TimerTask? = null
    //播放状态
    lateinit var playStatus: PlayStatus
    var isFirstStart = true

    override fun onCreate() {
        super.onCreate()
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
            initService()
        }
        return binder
    }

    private fun initService() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnBufferingUpdateListener(this)
        timer = Timer()
        mStatusObservers = ArrayList()

        try {
            val objectInputStream: ObjectInputStream = ObjectInputStream(openFileInput(STATUS_TMP_FILE_NAME))
            playStatus = objectInputStream.readObject() as PlayStatus
            objectInputStream.close()
        } catch (e: FileNotFoundException) {
            //第一次安装时
            playStatus = PlayStatus()
            isFirstStart = false //防止 onPrepare 第一次不播放歌曲
            LogUtil.i(TAG, "第一次安装")
        }
        if (playStatus.playProgress == 100f) {
            playStatus.playProgress = 0f
        }
        if (isFirstStart && playStatus.currentSong != null) {
            setStreamUrlAndPrepareAsync(playStatus.currentSong !!.listenFileUrl)
        }

    }

    @Suppress("DEPRECATION")
    fun playNewSong(song: Song) {
        //choose the same song,if media player is pause,then start;or do nothing
        if (playStatus.currentSong != null && playStatus.currentSong !!.songId == song.songId) {
            if (! isMediaPlaying()) {
                start()
            }
            return
        }

        if (isMediaPlaying()) {
            pause()
        }

        setStreamUrlAndPrepareAsync(song.listenFileUrl)
        playStatus.currentSong = song
        notifyStatusChanged(STATUS_NEW_SONG, song)
    }

    @Suppress("DEPRECATION")
    private fun setStreamUrlAndPrepareAsync(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(url)
        } catch (e: IllegalArgumentException) {
            notifyStatusChanged(STATUS_ERROR, "参数有误")
        } catch (e: IOException) {
            notifyStatusChanged(STATUS_ERROR, "文件打开失败")
        }
        mediaPlayer.prepareAsync()
    }

    override fun onPrepared(player: MediaPlayer?) {
        //第一次启动，并且 playStatus 是从文件中读取的
        //只定位不播放
        if (isFirstStart) {
            seekTo(playStatus.playProgress)
            isFirstStart = false
        } else {
            if (playStatus.currentSong !!.length == 0) {
                playStatus.currentSong !!.length = mediaPlayer.duration / 1000
            }
            start()

        }
    }


    override fun onSeekComplete(player: MediaPlayer?) {
        //进度条定位完成
//        notifyStatusChanged(STATUS_PLAY_PROCESS_UPDATE, getCurrentPlayProgress())
    }

    override fun onCompletion(player: MediaPlayer?) {
        // 播放完成
        notifyStatusChanged(STATUS_SONG_COMPLETED, null)
        updateProgressTask !!.cancel()
        insertOrUpdatePlayHistory()
        LogUtil.d(TAG, "播放完成")
    }

    private fun insertOrUpdatePlayHistory() {
        val dao = DAOUtil.getSession(this).playHistoryDao
        val queryList = dao.queryBuilder()
                .where(PlayHistoryDao.Properties.SongId.eq(playStatus.currentSong !!.songId))
                .build()
                .list()
        if (queryList.size > 0) {
            val playHistory = queryList[0]
            playHistory.playTimeInMills = System.currentTimeMillis()
            playHistory.playTimes += 1
            dao.update(playHistory)
        } else {
            val playHistory = PlayHistory(null, System.currentTimeMillis(), 1, playStatus.currentSong !!.name,
                    playStatus.currentSong !!.songId, playStatus.currentSong !!.artistId, playStatus.currentSong !!.albumId,
                    playStatus.currentSong !!.albumCoverUrl, playStatus.currentSong !!.artistName, playStatus.currentSong !!.albumName,
                    playStatus.currentSong !!.listenFileUrl, playStatus.currentSong !!.miniAlbumCoverUrl)
            dao.insert(playHistory)
        }
    }


    fun start() {
        if (playStatus.currentSong == null) {
            notifyStatusChanged(STATUS_INFO, "当前没有歌曲播放")
        } else {
            if (! mediaPlayer.isPlaying) {
                mediaPlayer.start()
                playStatus.isPlaying = true
                notifyStatusChanged(STATUS_START, null)
                updateProgressTask = ProgressTimerTask()
                timer.schedule(updateProgressTask, 0, 200)
            }
        }

    }

    fun pause() {
        if (isMediaPlaying()) {
            playStatus.isPlaying = false
            mediaPlayer.pause()
            notifyStatusChanged(STATUS_PAUSE, null)
            updateProgressTask !!.cancel()
        }
    }

    fun stop() {
        mediaPlayer.stop()
        playStatus.isPlaying = false
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
        //防止缓存一直停在99
        val adjustPercent = if (percent == 99) {
            percent + 1
        } else {
            percent
        }
        playStatus.bufferedProgress = adjustPercent
        notifyStatusChanged(STATUS_BUFFER_PROCESS_UPDATE, adjustPercent)
        LogUtil.d(TAG, "buffer adjust percent:$adjustPercent%")
    }

    override fun onError(player: MediaPlayer?, what: Int, extra: Int): Boolean {
//        var msg = when (what) {
//            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
//                "未知资源"
//            }
//            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
//                "与服务器连接失败"
//            }
//            else -> {
//                ""
//            }
//        }
        val msg = when (extra) {
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                "文件格式不支持"
            }
            MediaPlayer.MEDIA_ERROR_IO -> {
                "打开失败"
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                "连接超时"
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                "文件不符合编码格式"
            }
            else -> {
                ""
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

    fun changePlayMode(playMode: PlayMode) {
        playStatus.playMode = playMode
        notifyStatusChanged(STATUS_PLAY_MODE_CHANGED, playStatus.playMode)

    }

    fun getCurrentPlayProgress(): Float {
        return (mediaPlayer.currentPosition / mediaPlayer.duration.toFloat()) * 100
    }

    fun seekTo(percent: Float) {
        val millionSec = (percent / 100 * mediaPlayer.duration).toInt()
        mediaPlayer.seekTo(millionSec)
    }

    fun registerStatusObserver(observer: PlayStatusObserver) {
        observer.onPlayInit(playStatus)
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
                STATUS_PLAY_PROCESS_UPDATE -> observer.onPlayProgressUpdate(extra as Float)
                STATUS_ERROR -> observer.onPlayError(extra as String)
                STATUS_INFO -> observer.onPlayInfo(extra as String)
                STATUS_NEW_SONG -> observer.onNewSong(extra as Song)
                STATUS_SONG_COMPLETED -> observer.onSongCompleted()
                STATUS_PLAY_MODE_CHANGED -> observer.onPlayModeChanged(extra as PlayMode)
            }
        }

    }

    fun isMediaPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        if (playStatus.isPlaying) {
            playStatus.isPlaying = false
        }
        //save status
        val outputStream = openFileOutput(STATUS_TMP_FILE_NAME, Context.MODE_PRIVATE)
        val os: ObjectOutputStream = ObjectOutputStream(outputStream)
        os.writeObject(playStatus)
        os.close()

        mediaPlayer.release()
        LogUtil.d(TAG, "media player released")
        timer.cancel()
    }


    inner class ControlBinder : Binder() {
        fun getPlayService(): PlayService {
            return this@PlayService
        }
    }

    inner class ProgressTimerTask : TimerTask() {
        override fun run() {
            playStatus.playProgress = getCurrentPlayProgress()
            notifyStatusChanged(STATUS_PLAY_PROCESS_UPDATE, playStatus.playProgress)
        }

    }

    class PlayStatus : Serializable {
        val serialVersionUID: Long = 998
        var isPlaying: Boolean = false
        var currentSong: Song? = null
        var currentList: List<Song>? = null
        var playProgress: Float = 0f
        var bufferedProgress: Int = 0
        var playMode: PlayMode = PlayMode.REPEAT_LIST
    }


}
