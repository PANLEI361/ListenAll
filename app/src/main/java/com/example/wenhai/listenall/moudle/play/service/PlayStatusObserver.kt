package com.example.wenhai.listenall.moudle.play.service

import com.example.wenhai.listenall.data.bean.Song

interface PlayStatusObserver {
    fun onPlayInit(playStatus: PlayService.PlayStatus)

    fun onPlayStart()

    fun onPlayPause()

    fun onPlayStop()

    fun onPlayModeChanged(playMode: PlayService.PlayMode)

    //缓存进度更新
    fun onBufferProgressUpdate(percent: Int)

    //播放进度更新
    fun onPlayProgressUpdate(percent: Float)

    fun onPlayError(msg: String)

    fun onPlayInfo(msg: String)

    fun onNewSong(song: Song)

    fun onSongCompleted()
}