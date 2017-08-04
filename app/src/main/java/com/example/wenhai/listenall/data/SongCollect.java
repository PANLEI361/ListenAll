package com.example.wenhai.listenall.data;

/**
 * 歌单对应实体类
 * Created by Wenhai on 2017/7/30.
 */

public class SongCollect {

    private String title;
    private String desc;//简介
    private long collectId;//歌单 id
    private String coverUrl;//封面 url
    private int songNumber;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private OnlineMusicSource source;//来源
    private String playTimes;//播放次数

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

    public long getCollectId() {
        return collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
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

    public OnlineMusicSource getSource() {
        return source;
    }

    public void setSource(OnlineMusicSource source) {
        this.source = source;
    }

    public String getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(String playTimes) {
        this.playTimes = playTimes;
    }
}
