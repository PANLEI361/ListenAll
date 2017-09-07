package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LikedCollect {
    @Id
    private Long id;
    private String title;
    private String desc;//简介
    private long collectId;//歌单 id
    private String coverUrl;//封面 url
    private int coverDrawable;//封面 drawable
    private int songCount;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private String providerName;//来源
    private int playTimes;//播放次数
    private long createDate;//创建时间
    private long updateDate;//更新时间
    private long likedTime;

    @Generated(hash = 1099010734)
    public LikedCollect(Long id, String title, String desc, long collectId,
                        String coverUrl, int coverDrawable, int songCount,
                        int songDownloadNumber, String providerName, int playTimes,
                        long createDate, long updateDate, long likedTime) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.collectId = collectId;
        this.coverUrl = coverUrl;
        this.coverDrawable = coverDrawable;
        this.songCount = songCount;
        this.songDownloadNumber = songDownloadNumber;
        this.providerName = providerName;
        this.playTimes = playTimes;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.likedTime = likedTime;
    }

    @Generated(hash = 1076277660)
    public LikedCollect() {
    }

    public LikedCollect(Collect collect) {
        this.title = collect.getTitle();
        this.desc = collect.getDesc();
        this.collectId = collect.getId();
        this.coverUrl = collect.getCoverUrl();
        this.coverDrawable = collect.getCoverDrawable();
        this.songCount = collect.getSongCount();
        this.songDownloadNumber = collect.getSongDownloadNumber();
        this.providerName = collect.getSource().name();
        this.playTimes = collect.getPlayTimes();
        this.createDate = collect.getCreateDate();
        this.updateDate = collect.getUpdateDate();
        this.likedTime = System.currentTimeMillis();
    }

    public Collect getCollect() {
        Collect collect = new Collect();
        collect.setTitle(title);
        collect.setDesc(desc);
        collect.setId(collectId);
        collect.setCoverUrl(coverUrl);
        collect.setCoverDrawable(coverDrawable);
        collect.setSongCount(songCount);
        collect.setSongDownloadNumber(songDownloadNumber);
        MusicProvider provider = MusicProvider.valueOf(providerName);
        collect.setSource(provider);
        collect.setPlayTimes(playTimes);
        collect.setCreateDate(createDate);
        collect.setUpdateDate(updateDate);
        return collect;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getCollectId() {
        return this.collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCoverDrawable() {
        return this.coverDrawable;
    }

    public void setCoverDrawable(int coverDrawable) {
        this.coverDrawable = coverDrawable;
    }

    public int getSongCount() {
        return this.songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public int getSongDownloadNumber() {
        return this.songDownloadNumber;
    }

    public void setSongDownloadNumber(int songDownloadNumber) {
        this.songDownloadNumber = songDownloadNumber;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getPlayTimes() {
        return this.playTimes;
    }

    public void setPlayTimes(int playTimes) {
        this.playTimes = playTimes;
    }

    public long getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public long getLikedTime() {
        return this.likedTime;
    }

    public void setLikedTime(long likedTime) {
        this.likedTime = likedTime;
    }
}
