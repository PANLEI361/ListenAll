package com.example.wenhai.listenall.moudle.play.service

import com.example.wenhai.listenall.data.bean.Song

interface PlayProxy {
    fun setNextSong(song: Song): Boolean

    fun playSong(song: Song)
}