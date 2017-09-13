package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LikedSong {
    @Id
    private Long id;
    private String songName;
    private long songId;
    private long artistId;
    private long albumId;
    private String coverUrl;
    private String artistName;
    private String albumName;
    private String listenFileUrl;
    private String miniAlbumCoverUrl;
    private String providerName;
    private long likedTime;

    @Generated(hash = 1529005998)
    public LikedSong(Long id, String songName, long songId, long artistId, long albumId,
                     String coverUrl, String artistName, String albumName, String listenFileUrl,
                     String miniAlbumCoverUrl, String providerName, long likedTime) {
        this.id = id;
        this.songName = songName;
        this.songId = songId;
        this.artistId = artistId;
        this.albumId = albumId;
        this.coverUrl = coverUrl;
        this.artistName = artistName;
        this.albumName = albumName;
        this.listenFileUrl = listenFileUrl;
        this.miniAlbumCoverUrl = miniAlbumCoverUrl;
        this.providerName = providerName;
        this.likedTime = likedTime;
    }

    @Generated(hash = 838669917)
    public LikedSong() {
    }

    public LikedSong(Song song) {
        songName = song.getName();
        songId = song.getSongId();
        artistId = song.getArtistId();
        albumId = song.getAlbumId();
        albumName = song.getAlbumName();
        coverUrl = song.getAlbumCoverUrl();
        artistName = song.getArtistName();
        listenFileUrl = song.getListenFileUrl();
        miniAlbumCoverUrl = song.getMiniAlbumCoverUrl();
        providerName = song.getSupplier().name();
        likedTime = System.currentTimeMillis();
    }

    public Song getSong() {
        Song song = new Song();
        song.setName(songName);
        song.setSongId(songId);
        song.setArtistId(artistId);
        song.setArtistName(artistName);
        song.setAlbumId(albumId);
        song.setAlbumCoverUrl(coverUrl);
        song.setAlbumName(albumName);
        song.setListenFileUrl(listenFileUrl);
        song.setMiniAlbumCoverUrl(miniAlbumCoverUrl);
        MusicProvider provider = MusicProvider.valueOf(providerName);
        song.setSupplier(provider);
        return song;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSongName() {
        return this.songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public long getSongId() {
        return this.songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getArtistId() {
        return this.artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getListenFileUrl() {
        return this.listenFileUrl;
    }

    public void setListenFileUrl(String listenFileUrl) {
        this.listenFileUrl = listenFileUrl;
    }

    public String getMiniAlbumCoverUrl() {
        return this.miniAlbumCoverUrl;
    }

    public void setMiniAlbumCoverUrl(String miniAlbumCoverUrl) {
        this.miniAlbumCoverUrl = miniAlbumCoverUrl;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public long getLikedTime() {
        return this.likedTime;
    }

    public void setLikedTime(long likedTime) {
        this.likedTime = likedTime;
    }
}
