package com.example.wenhai.listenall.moudle.artist.detail

import com.example.wenhai.listenall.data.LoadArtistAlbumsCallback
import com.example.wenhai.listenall.data.LoadArtistDetailCallback
import com.example.wenhai.listenall.data.LoadArtistHotSongsCallback
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicRepository
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song

internal class ArtistDetailPresenter(val view: ArtistDetailContract.View) : ArtistDetailContract.Presenter {


    val musicRepository: MusicRepository = MusicRepository()

    init {
        view.setPresenter(this)
    }

    override fun loadArtistHotSongs(artist: Artist) {
        musicRepository.loadArtistHotSongs(artist, object : LoadArtistHotSongsCallback {
            override fun onFailure() {
                view.onFailure("获取艺人信息失败")
            }

            override fun onSuccess(hotSongs: List<Song>) {
                view.onHotSongsLoad(hotSongs)
            }

        })

    }

    override fun loadArtistAlbums(artist: Artist) {
        musicRepository.loadArtistAlbums(artist, object : LoadArtistAlbumsCallback {
            override fun onFailure() {
                view.onFailure("获取艺人专辑失败")
            }

            override fun onSuccess(albums: List<Album>) {
                view.onAlbumsLoad(albums)
            }

        })

    }

    override fun loadArtistDetail(artist: Artist) {
        musicRepository.loadArtistDetail(artist, object : LoadArtistDetailCallback {
            override fun onFailure() {
                view.onFailure("获取艺人详情失败")
            }

            override fun onSuccess(artistDetail: Artist) {
                view.onArtistDetail(artistDetail)
            }

        })

    }

    override fun loadSongDetail(song: Song) {
        musicRepository.loadSongDetail(song, object : LoadSongDetailCallback {
            override fun onFailure() {
                view.onFailure("当前歌曲无法播放")
            }

            override fun onSuccess(loadedSong: Song) {
                view.onSongDetailLoaded(loadedSong)
            }
        })
    }

}