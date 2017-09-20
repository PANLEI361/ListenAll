package com.example.wenhai.listenall.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.wenhai.listenall.data.MusicProvider;

import java.util.ArrayList;

/**
 * 歌单对应实体类
 * Created by Wenhai on 2017/7/30.
 */

public class Collect implements Parcelable {
    private String title;
    private String desc;//简介
    private long id;//歌单 id
    private String coverUrl;//封面 url
    private int coverDrawable;//封面 drawable
    private int songCount;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private MusicProvider source;//来源
    private int playTimes;//播放次数
    private long createDate;//创建时间
    private long updateDate;//更新时间
    private ArrayList<Song> songs;//歌曲
    private boolean isFromUser = false;//是否是用户创建的

    public Collect() {

    }

    protected Collect(Parcel in) {
        title = in.readString();
        desc = in.readString();
        id = in.readLong();
        coverUrl = in.readString();
        coverDrawable = in.readInt();
        songCount = in.readInt();
        songDownloadNumber = in.readInt();
        playTimes = in.readInt();
        createDate = in.readLong();
        updateDate = in.readLong();
        songs = in.createTypedArrayList(Song.CREATOR);
        isFromUser = in.readInt() == 1;
    }

    public static final Creator<Collect> CREATOR = new Creator<Collect>() {
        @Override
        public Collect createFromParcel(Parcel in) {
            return new Collect(in);
        }

        @Override
        public Collect[] newArray(int size) {
            return new Collect[size];
        }
    };

    public int getCoverDrawable() {
        return coverDrawable;
    }

    public void setCoverDrawable(int coverDrawable) {
        this.coverDrawable = coverDrawable;
    }

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

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
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

    public boolean isFromUser() {
        return isFromUser;
    }

    public void setFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }

    @Override
    public String toString() {
        return "Collect{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeLong(id);
        parcel.writeString(coverUrl);
        parcel.writeInt(coverDrawable);
        parcel.writeInt(songCount);
        parcel.writeInt(songDownloadNumber);
        parcel.writeInt(playTimes);
        parcel.writeLong(createDate);
        parcel.writeLong(updateDate);
        parcel.writeTypedList(songs);
        parcel.writeInt(isFromUser ? 1 : 0);
    }
}
