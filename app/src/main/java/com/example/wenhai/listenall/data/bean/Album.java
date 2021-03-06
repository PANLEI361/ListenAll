package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import java.util.List;

public class Album {
    private String title;
    private String desc;//简介
    private long id;//专辑 id
    private String artist;
    private long artistId;
    private String coverUrl;//封面 url
    private String miniCoverUrl;//封面 url
    private int songNumber;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private long publishDate;//发行时间
    private String publishDateStr;
    private MusicProvider supplier;
    private List<Song> songs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMiniCoverUrl() {
        return miniCoverUrl;
    }

    public void setMiniCoverUrl(String miniCoverUrl) {
        this.miniCoverUrl = miniCoverUrl;
    }

    public int getSongNumber() {
        return songNumber;
    }

    public void setSongNumber(int songNumber) {
        this.songNumber = songNumber;
    }

    public int getSongDownloadNumber() {
        return songDownloadNumber;
    }

    public void setSongDownloadNumber(int songDownloadNumber) {
        this.songDownloadNumber = songDownloadNumber;
    }

    public String getPublishDateStr() {
        return publishDateStr;
    }

    public void setPublishDateStr(String publishDateStr) {
        this.publishDateStr = publishDateStr;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public MusicProvider getSupplier() {
        return supplier;
    }

    public void setSupplier(MusicProvider supplier) {
        this.supplier = supplier;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
