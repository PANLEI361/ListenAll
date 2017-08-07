package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicSupplier;

import java.util.List;

public class Album {
    private String title;
    private String desc;//简介
    private long id;//专辑 id
    private String artist;
    private long artistId;
    private String coverUrl;//封面 url
    private int songNumber;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private MusicSupplier source;//来源
    private long publishDate;//发行时间
    private MusicSupplier supplier;
    private List<Song> songs;

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public MusicSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(MusicSupplier supplier) {
        this.supplier = supplier;
    }

    public MusicSupplier getSource() {
        return source;
    }

    public void setSource(MusicSupplier source) {
        this.source = source;
    }
}
