package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import java.util.ArrayList;

/**
 * 歌单对应实体类
 * Created by Wenhai on 2017/7/30.
 */

public class Collect {
    private String title;
    private String desc;//简介
    private long id;//歌单 id
    private String coverUrl;//封面 url
    private int songCount;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private MusicProvider source;//来源
    private int playTimes;//播放次数
    private long createDate;//创建时间
    private ArrayList<Song> songs;//歌曲

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public int getSongDownloadNumber() {
        return songDownloadNumber;
    }

    public void setSongDownloadNumber(int songDownloadNumber) {
        this.songDownloadNumber = songDownloadNumber;
    }

    public MusicProvider getSource() {
        return source;
    }

    public void setSource(MusicProvider source) {
        this.source = source;
    }

    public int getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(int playTimes) {
        this.playTimes = playTimes;
    }

    @Override
    public String toString() {
        return "Collect{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
