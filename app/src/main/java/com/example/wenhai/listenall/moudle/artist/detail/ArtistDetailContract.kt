package com.example.wenhai.listenall.moudle.artist.detail

import com.example.wenhai.listenall.base.BasePresenter
import com.example.wenhai.listenall.base.BaseView
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Song

interface ArtistDetailContract {
    interface Presenter : BasePresenter {
        //获取信息和图片
        fun loadArtistDetail(artist: Artist)

        fun loadArtistHotSongs(artist: Artist)
        fun loadArtistAlbums(artist: Artist)
        fun loadSongDetail(song: Song)
    }

    interface View : BaseView<Presenter> {
        fun onFailure(msg: String)
        fun onArtistDetail(artist: Artist)
        fun onHotSongsLoad(hotSongs: List<Song>)
        fun onAlbumsLoad(albums: List<Album>)
        fun onSongDetailLoaded(song: Song)

    }

}