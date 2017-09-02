package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LikedAlbum {
    @Id
    private Long id;
    private String title;
    private String desc;//简介
    private long albumId;//专辑 id
    private String artist;
    private long artistId;
    private String coverUrl;//封面 url
    private String miniCoverUrl;//封面 url
    private int songNumber;//包含歌曲数量
    private int songDownloadNumber;//已下载歌曲数量
    private String providerName;//来源
    private long publishDate;//发行时间
    private String publishDateStr;

    @Generated(hash = 304949634)
    public LikedAlbum(Long id, String title, String desc, long albumId,
                      String artist, long artistId, String coverUrl, String miniCoverUrl,
                      int songNumber, int songDownloadNumber, String providerName,
                      long publishDate, String publishDateStr) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.albumId = albumId;
        this.artist = artist;
        this.artistId = artistId;
        this.coverUrl = coverUrl;
        this.miniCoverUrl = miniCoverUrl;
        this.songNumber = songNumber;
        this.songDownloadNumber = songDownloadNumber;
        this.providerName = providerName;
        this.publishDate = publishDate;
        this.publishDateStr = publishDateStr;
    }

    @Generated(hash = 2061804808)
    public LikedAlbum() {
    }

    public LikedAlbum(Album album) {
        title = album.getTitle();
        desc = album.getDesc();
        albumId = album.getId();
        artist = album.getArtist();
        artistId = album.getArtistId();
        coverUrl = album.getCoverUrl();
        miniCoverUrl = album.getMiniCoverUrl();
        songNumber = album.getSongNumber();
        songDownloadNumber = album.getSongDownloadNumber();
        publishDate = album.getPublishDate();
        publishDateStr = album.getPublishDateStr();
        providerName = album.getSupplier().name();
    }

    public Album getAlbum() {
        Album album = new Album();
        album.setTitle(title);
        album.setDesc(desc);
        album.setId(albumId);
        album.setArtist(artist);
        album.setArtistId(artistId);
        album.setCoverUrl(coverUrl);
        album.setMiniCoverUrl(miniCoverUrl);
        album.setSongNumber(songNumber);
        album.setSongDownloadNumber(songDownloadNumber);
        album.setPublishDate(publishDate);
        album.setPublishDateStr(publishDateStr);
        MusicProvider provider;
        if (providerName.equals(MusicProvider.NETEASE.name())) {
            provider = MusicProvider.NETEASE;
        } else if (providerName.equals(MusicProvider.QQMUSIC.name())) {
            provider = MusicProvider.QQMUSIC;
        } else {
            provider = MusicProvider.XIAMI;
        }
        album.setSupplier(provider);
        return album;
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

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtistId() {
        return this.artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMiniCoverUrl() {
        return this.miniCoverUrl;
    }

    public void setMiniCoverUrl(String miniCoverUrl) {
        this.miniCoverUrl = miniCoverUrl;
    }

    public int getSongNumber() {
        return this.songNumber;
    }

    public void setSongNumber(int songNumber) {
        this.songNumber = songNumber;
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

    public long getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDateStr() {
        return this.publishDateStr;
    }

    public void setPublishDateStr(String publishDateStr) {
        this.publishDateStr = publishDateStr;
    }

    @Override
    public String toString() {
        return "LikedAlbum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", albumId=" + albumId +
                ", artist='" + artist + '\'' +
                ", artistId=" + artistId +
                ", coverUrl='" + coverUrl + '\'' +
                ", miniCoverUrl='" + miniCoverUrl + '\'' +
                ", songNumber=" + songNumber +
                ", songDownloadNumber=" + songDownloadNumber +
                ", providerName='" + providerName + '\'' +
                ", publishDate=" + publishDate +
                ", publishDateStr='" + publishDateStr + '\'' +
                '}';
    }
}
