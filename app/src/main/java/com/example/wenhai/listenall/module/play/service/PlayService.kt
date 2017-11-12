package com.example.wenhai.listenall.module.play.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import com.example.wenhai.listenall.BuildConfig
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.PlayHistory
import com.example.wenhai.listenall.data.bean.PlayHistoryDao
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.utils.DAOUtil
import com.example.wenhai.listenall.utils.LogUtil
import com.example.wenhai.listenall.utils.OkHttpUtil
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.ArrayList

class PlayService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener {

    private var isFirstStart = true
    private val binder: Binder = ServiceBinder()

    private lateinit var musicRepository: MusicRepository
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var playStatus: PlayStatus//播放状态
    private lateinit var mStatusObservers: ArrayList<PlayStatusObserver>

    //用于更新播放进度的Timer和TimerTask
    private lateinit var timer: Timer
    private var updateProgressTask: TimerTask? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent?.action == ACTION_INIT) {
//        }
        if (intent?.action == ACTION_NEW_SONG) {
            val song = intent.getParcelableExtra<Song>("song")
            playNewSong(song)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        when (intent.action) {
            ACTION_INIT -> {//执行初始化
                initService()
            }
            ACTION_NEW_SONG -> {//播放新歌曲
                val song = intent.getParcelableExtra<Song>("song")
                playNewSong(song)
            }
        }
        return binder
    }


    private fun initService() {
        //初始化 MediaPlayer
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnBufferingUpdateListener(this)

        //初始化其他变量
        timer = Timer()
        musicRepository = MusicRepository.getInstance(this)
        mStatusObservers = ArrayList()

        //从临时文件恢复播放状态
        recoverPlayStatusFromFile()

    }

    private fun recoverPlayStatusFromFile() {
        var objectInputStream: ObjectInputStream? = null
        try {
            objectInputStream = ObjectInputStream(openFileInput(STATUS_TMP_FILE_NAME))
            playStatus = objectInputStream.readObject() as PlayStatus
            objectInputStream.close()
        } catch (e: IOException) {
            LogUtil.e(TAG, "读取文件出错")
            //出错后直接新建一个 playStatus
            playStatus = PlayStatus()
            //防止第一次 onPrepare 不播放歌曲
            isFirstStart = false
        } finally {
            objectInputStream?.close()
        }

        if (playStatus.playProgress == 100f) {
            //如果上次播放已完成，则进度调整到0
            playStatus.playProgress = 0f
            notifyPlayStatusChanged(STATUS_PLAY_PROCESS_UPDATE, playStatus.playProgress)
        }
        if (isFirstStart && playStatus.currentSong != null) {
            setSongAndPrepareAsync(playStatus.currentSong!!)
        }
    }


    @Suppress("DEPRECATION")
    fun playNewSong(newSong: Song) {
        var newPlaySong = newSong
        //如果选择歌曲为当前正在播放歌曲并且当前播放状态为暂停，那么继续播放
        if (playStatus.currentSong?.songId == newPlaySong.songId) {
            if (!isMediaPlaying()) {
                start()
            }
            return
        }

        if (isMediaPlaying()) {
            pause()
        }

        //从当前播放列表查找待播放歌曲
        val addedSong = findInCurList(newPlaySong)
        if (addedSong == null) {
            //没有找到，将歌曲添加到当前播放列表
            playStatus.currentList.add(newPlaySong)
        } else {
            newPlaySong = addedSong
        }

        //如果歌曲播放url为空，那么通过网络加载歌曲播放地址后进行播放；否则直接播放歌曲
        if (TextUtils.isEmpty(newPlaySong.listenFileUrl)) {
            musicRepository.loadSongDetail(newSong, object : LoadSongDetailCallback {
                override fun onStart() {

                }

                override fun onFailure(msg: String) {
                    notifyPlayStatusChanged(STATUS_INFO, msg)
                }

                override fun onSuccess(loadedSong: Song) {
                    setSongAndPrepareAsync(loadedSong)
                }

            })
        } else {
            setSongAndPrepareAsync(newPlaySong)
        }
    }

    /**
     * 设置播放歌曲
     */
    @Suppress("DEPRECATION")
    private fun setSongAndPrepareAsync(song: Song) {
        try {
            //检查网络状态
            val bundle = OkHttpUtil.checkNetWork(this)
            //网络可用，进行设置
            if (bundle.getInt(OkHttpUtil.ARG_NETWORK_STATE) == OkHttpUtil.NETWORK_AVAILABLE) {
                mediaPlayer.reset()
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setDataSource(song.listenFileUrl)
                mediaPlayer.prepareAsync()
                playStatus.currentSong = song
                notifyPlayStatusChanged(STATUS_NEW_SONG, song)
            } else {//发送提示消息
                notifyPlayStatusChanged(STATUS_INFO, bundle.getString(OkHttpUtil.ARG_NETWORK_DESC))
            }
        } catch (e: IllegalArgumentException) {
            notifyPlayStatusChanged(STATUS_ERROR, "参数有误")
            mediaPlayer.reset()
        } catch (e: IOException) {
            notifyPlayStatusChanged(STATUS_ERROR, "文件打开失败")
            mediaPlayer.reset()
        }
    }

    /**
     * 在当前列表中查找指定歌曲
     */
    private fun findInCurList(song: Song): Song? {
        return playStatus.currentList.firstOrNull { it.songId == song.songId }
    }

    /**
     * 设置播放歌曲准备完成时回调
     */
    override fun onPrepared(player: MediaPlayer?) {
        //第一次启动，并且 playStatus 是从文件中读取的
        //只定位不播放
        if (isFirstStart) {
            seekTo(playStatus.playProgress)
            isFirstStart = false
        } else {
            if (playStatus.currentSong?.length == 0) {
                playStatus.currentSong?.length = mediaPlayer.duration / 1000
            }
            start()
        }
    }

    /**
     * 进度条定位完成
     *
     */
    override fun onSeekComplete(player: MediaPlayer?) {
        notifyPlayStatusChanged(STATUS_PLAY_PROCESS_UPDATE, getPercentPlayProgress())
    }

    /**
     * 播放完成后回调
     */
    override fun onCompletion(player: MediaPlayer?) {
        // 设置状态为暂停
        playStatus.isPlaying = false

        notifyPlayStatusChanged(STATUS_PAUSE, null)
        notifyPlayStatusChanged(STATUS_SONG_COMPLETED, null)
        //取消播放进度更新
        updateProgressTask!!.cancel()
        insertOrUpdatePlayHistory()
        //调整进度
        playStatus.playProgress = 100f
        LogUtil.d(TAG, "播放完成")
        LogUtil.d(TAG, "开始播放下一首")
        next()
    }

    /**
     * 插入或者更新歌曲播放历史
     */
    private fun insertOrUpdatePlayHistory() {
        val dao = DAOUtil.getSession(this).playHistoryDao
        val queryList = dao.queryBuilder()
                .where(PlayHistoryDao.Properties.SongId.eq(playStatus.currentSong!!.songId))
                .build()
                .list()
        if (queryList.size > 0) {
            val playHistory = queryList.first()
            //更新播放时间和次数
            playHistory.playTimeInMills = System.currentTimeMillis()
            playHistory.playTimes += 1
            dao.update(playHistory)
        } else {
            //创建新的播放纪录
            val playHistory = PlayHistory(playStatus.currentSong)
            dao.insert(playHistory)
        }
    }


    fun start() {
        if (playStatus.currentSong == null) {
            notifyPlayStatusChanged(STATUS_INFO, "当前没有歌曲播放")
        } else if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            playStatus.isPlaying = true
            notifyPlayStatusChanged(STATUS_START, null)
            updateProgressTask = ProgressTimerTask()
            timer.schedule(updateProgressTask, 0, 200)
        }
    }

    fun pause() {
        if (isMediaPlaying()) {
            mediaPlayer.pause()
            playStatus.isPlaying = false
            notifyPlayStatusChanged(STATUS_PAUSE, null)
            updateProgressTask?.cancel()
        }
    }

//    fun stop() {
//        mediaPlayer.stop()
//        playStatus.isPlaying = false
//        notifyPlayStatusChanged(STATUS_STOP, null)
//        updateProgressTask?.cancel()
//    }


    /**
     * 缓存进度更新
     */
    override fun onBufferingUpdate(player: MediaPlayer?, percent: Int) {
        //防止缓存一直停在99
        val adjustPercent = if (percent == 99) {
            percent + 1
        } else {
            percent
        }
        playStatus.bufferedProgress = adjustPercent
        notifyPlayStatusChanged(STATUS_BUFFER_PROCESS_UPDATE, adjustPercent)
    }

    //播放器错误信息
    override fun onError(player: MediaPlayer?, what: Int, extra: Int): Boolean {
        LogUtil.e("test", "onError")
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
        notifyPlayStatusChanged(STATUS_ERROR, msg)
        return true
    }

    //播放器提示信息
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
        notifyPlayStatusChanged(STATUS_INFO, msg)
        return true
    }

    /**
     * 下一曲
     */
    fun next() {
        val nextIndex = arrangeNextSongIndexByMode()
        playNewSong(playStatus.currentList[nextIndex])
    }

    /**
     * 根据播放模式确定下一首歌曲索引
     */
    private fun arrangeNextSongIndexByMode(): Int {
        val curIndex = playStatus.currentList.indexOf(playStatus.currentSong)
        return when (playStatus.playMode) {
            PlayMode.REPEAT_LIST -> {//列表循环
                if (curIndex == (playStatus.currentList.size - 1)) {
                    0
                } else {
                    curIndex + 1
                }
            }
            PlayMode.REPEAT_ONE -> {//单曲循环
                curIndex
            }
            PlayMode.SHUFFLE -> {//随机播放
                Random(System.currentTimeMillis()).nextInt(playStatus.currentList.size)
            }
        }
    }

    /**
     * 上一曲
     */
    fun previous() {
        val nextIndex = arrangePreviousSongIndexByMode()
        playNewSong(playStatus.currentList[nextIndex])
    }

    /**
     * 根据播放模式确定上一首歌曲索引
     */
    private fun arrangePreviousSongIndexByMode(): Int {
        val curIndex = playStatus.currentList.indexOf(playStatus.currentSong)
        return when (playStatus.playMode) {
            PlayMode.REPEAT_LIST -> {
                if (curIndex == 0) {
                    playStatus.currentList.size - 1
                } else {
                    curIndex - 1
                }
            }
            PlayMode.REPEAT_ONE -> {
                curIndex
            }
            PlayMode.SHUFFLE -> {
                Random(System.currentTimeMillis()).nextInt(playStatus.currentList.size)
            }
        }
    }

    /**
     * 将歌曲添加到当前播放列表
     */
    fun addToPlayList(songs: List<Song>) {
        songs.filterNot { playStatus.currentList.contains(it) }
                .forEach { playStatus.currentList.add(it) }
        notifyPlayStatusChanged(STATUS_INFO, getString(R.string.play_has_added_to_cur_list))
    }

    /**
     * 随机播放全部
     */
    fun shuffleAll(songs: List<Song>) {
        setPlayMode(PlayMode.SHUFFLE)
        replaceList(songs)
    }

    /**
     * 设置歌曲下一首播放
     */
    fun setNextSong(song: Song): Boolean {
        val curIndex = playStatus.currentList.indexOf(playStatus.currentSong)
        return if (playStatus.currentList.contains(song)) {
            false
        } else {
            playStatus.currentList.add(curIndex + 1, song)
            true
        }
    }

    /**
     * 更换当前播放列表
     */
    fun replaceList(songs: List<Song>) {
        playStatus.currentList.clear()
        playStatus.currentList.addAll(songs)
        notifyPlayStatusChanged(STATUS_NEW_LIST, null)
        playNewSong(playStatus.currentList.first())
    }

    /**
     * 改变播放模式：单曲循环、列表循环、随机播放
     */
    fun changePlayMode() {
        playStatus.playMode = when (playStatus.playMode) {
            PlayMode.REPEAT_LIST -> {
                PlayMode.SHUFFLE
            }
            PlayMode.SHUFFLE -> {
                PlayMode.REPEAT_ONE
            }
            PlayMode.REPEAT_ONE -> {
                PlayMode.REPEAT_LIST
            }
        }
        notifyPlayStatusChanged(STATUS_PLAY_MODE_CHANGED, playStatus.playMode)
    }

    /**
     * 设定播放模式
     */
    private fun setPlayMode(mode: PlayMode) {
        playStatus.playMode = mode
        notifyPlayStatusChanged(STATUS_PLAY_MODE_CHANGED, playStatus.playMode)
    }

    /**
     * 获取当前播放进度
     */
    fun getPercentPlayProgress(): Float {
        return (mediaPlayer.currentPosition / mediaPlayer.duration.toFloat()) * 100
    }

    /**
     * 定位到指定进度（百分比）
     */
    fun seekTo(percent: Float) {
        val millionSec = (percent / 100 * mediaPlayer.duration).toInt()
        mediaPlayer.seekTo(millionSec)
    }

    /**
     * 注册播放状态观察者
     */
    fun registerStatusObserver(observer: PlayStatusObserver) {
        observer.onPlayInit(playStatus)
        mStatusObservers.add(observer)
    }

    /**
     * 注销播放状态观察者
     */
    fun unregisterStatusObserver(observer: PlayStatusObserver) {
        mStatusObservers.remove(observer)
    }

    /**
     * 通知观察者播放状态更新
     */
    fun notifyPlayStatusChanged(status: Int, extra: Any?) {
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
                STATUS_NEW_LIST -> observer.onNewSongList()
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
        //保存播放状态
        var os: ObjectOutputStream? = null
        try {
            val fileOutputStream = openFileOutput(STATUS_TMP_FILE_NAME, Context.MODE_PRIVATE)
            os = ObjectOutputStream(fileOutputStream)
            os.writeObject(playStatus)
        } finally {
            os?.close()
        }

        //释放播放器资源
        mediaPlayer.release()
        LogUtil.d(TAG, "media player released")
        timer.cancel()
    }

    companion object {
        const val TAG = "PlayService"

        const val STATUS_TMP_FILE_NAME = "play_status.tmp"
        const val ACTION_NEW_SONG = BuildConfig.APPLICATION_ID + "newsong"
        const val ACTION_INIT = BuildConfig.APPLICATION_ID + "init"

        const val STATUS_STOP = 0x00
        const val STATUS_START = 0x01
        const val STATUS_PAUSE = 0x02
        const val STATUS_BUFFER_PROCESS_UPDATE = 0x03
        const val STATUS_PLAY_PROCESS_UPDATE = 0x04
        const val STATUS_ERROR = 0x05
        const val STATUS_INFO = 0x06
        const val STATUS_NEW_SONG = 0x07
        const val STATUS_SONG_COMPLETED = 0x08
        const val STATUS_PLAY_MODE_CHANGED = 0x09
        const val STATUS_NEW_LIST = 0x0a
    }

    enum class PlayMode : Serializable {
        REPEAT_ONE, REPEAT_LIST, SHUFFLE
    }

    inner class ServiceBinder : Binder() {
        fun getPlayService(): PlayService {
            return this@PlayService
        }
    }

    inner class ProgressTimerTask : TimerTask() {
        override fun run() {
            playStatus.playProgress = getPercentPlayProgress()
            notifyPlayStatusChanged(STATUS_PLAY_PROCESS_UPDATE, playStatus.playProgress)
        }

    }

    class PlayStatus : Serializable {
        var isPlaying: Boolean = false
            set(value) {
                currentSong?.isPlaying = value
                field = value
            }
        var currentSong: Song? = null
        var currentList: ArrayList<Song> = ArrayList()
        var playProgress: Float = 0f
        var bufferedProgress: Int = 0
        var playMode: PlayMode = PlayMode.REPEAT_LIST
    }
}
