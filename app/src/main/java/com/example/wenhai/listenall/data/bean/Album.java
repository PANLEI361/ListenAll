package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicSupplier;

/**
 * Created by Wenhai on 2017/8/4.
 */

public class Album {
    private String title;
    private String desc;//简介
    private long id;//专辑 id
    private String artist;
    private String coverUrl;//封面 url
    private int songNumber;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private MusicSupplier source;//来源
    private long publishDate;//发行时间
    private MusicSupplier supplier;

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

    public MusicSupplier getSource() {
        return source;
    }

    public void setSource(MusicSupplier source) {
        this.source = source;
    }
}
