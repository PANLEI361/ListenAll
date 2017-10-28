package com.example.wenhai.listenall.data.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class JoinCollectsWithSongs {
    @Id
    private Long id;
    private Long songId;
    private Long collectId;

    @Generated(hash = 525280217)
    public JoinCollectsWithSongs(Long id, Long songId, Long collectId) {
        this.id = id;
        this.songId = songId;
        this.collectId = collectId;
    }

    @Generated(hash = 1835803374)
    public JoinCollectsWithSongs() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSongId() {
        return this.songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getCollectId() {
        return this.collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public static JoinCollectsWithSongs newRecord(long songId, long collectId) {
        JoinCollectsWithSongs record = new JoinCollectsWithSongs();
        record.songId = songId;
        record.collectId = collectId;
        return record;
    }
}
