package com.example.wenhai.listenall.data.bean;

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

    @Generated(hash = 1261294028)
    public PlayHistory(Long id, long playTimeInMills, int playTimes,
                       String songName, long songId, long artistId, long albumId,
                       String coverUrl, String artistName, String albumName,
                       String listenFileUrl, String miniAlbumCoverUrl) {
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
    }

    @Generated(hash = 2145518983)
    public PlayHistory() {
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

}
