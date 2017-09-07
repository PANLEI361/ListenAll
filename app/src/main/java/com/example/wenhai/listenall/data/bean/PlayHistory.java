package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class PlayHistory {
    @Id
    private Long id;
    private long playTimeInMills;
    private int playTimes;
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

    @Generated(hash = 1568489359)
    public PlayHistory(Long id, long playTimeInMills, int playTimes,
                       String songName, long songId, long artistId, long albumId,
                       String coverUrl, String artistName, String albumName,
                       String listenFileUrl, String miniAlbumCoverUrl, String providerName) {
        this.id = id;
        this.playTimeInMills = playTimeInMills;
        this.playTimes = playTimes;
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
    }

    @Generated(hash = 2145518983)
    public PlayHistory() {
    }

    public PlayHistory(Song song) {
        this.playTimeInMills = System.currentTimeMillis();
        this.playTimes = 0;
        this.songName = song.getName();
        this.songId = song.getSongId();
        this.artistId = song.getArtistId();
        this.albumId = song.getAlbumId();
        this.coverUrl = song.getAlbumCoverUrl();
        this.artistName = song.getArtistName();
        this.albumName = song.getAlbumName();
        this.listenFileUrl = song.getListenFileUrl();
        this.miniAlbumCoverUrl = song.getMiniAlbumCoverUrl();
        this.providerName = song.getSupplier().name();
    }

    public Song getSong() {
        Song song = new Song();
        song.setName(songName);
        song.setSongId(songId);
        song.setArtistId(artistId);
        song.setAlbumId(albumId);
        song.setAlbumCoverUrl(coverUrl);
        song.setArtistName(artistName);
        song.setAlbumName(albumName);
        song.setListenFileUrl(listenFileUrl);
        song.setMiniAlbumCoverUrl(miniAlbumCoverUrl);
        MusicProvider provider;
        if (providerName.equals(MusicProvider.NETEASE.name())) {
            provider = MusicProvider.NETEASE;
        } else if (providerName.equals(MusicProvider.QQMUSIC.name())) {
            provider = MusicProvider.QQMUSIC;
        } else {
            provider = MusicProvider.XIAMI;
        }
        song.setSupplier(provider);
        return song;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPlayTimeInMills() {
        return this.playTimeInMills;
    }

    public void setPlayTimeInMills(long playTimeInMills) {
        this.playTimeInMills = playTimeInMills;
    }

    public int getPlayTimes() {
        return this.playTimes;
    }

    public void setPlayTimes(int playTimes) {
        this.playTimes = playTimes;
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

}
