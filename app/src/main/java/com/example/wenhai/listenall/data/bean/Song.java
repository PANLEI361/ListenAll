package com.example.wenhai.listenall.data.bean;

import com.example.wenhai.listenall.data.MusicProvider;


public class Song {
    private long songId;
    private String name;

    private int length;//second
    private String listenFileUrl;//xiami
    private String lyricUrl;//lyric
    private int payFlag;//是否需要付费
    private boolean canFreeListen;
    private boolean canFreeDownload;

    private String artistName;
    private String artistLogo;
    private long artistId;

    private long albumId;
    private String albumName;
    private String albumCoverUrl;

    private MusicProvider supplier;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistLogo() {
        return artistLogo;
    }

    public void setArtistLogo(String artistLogo) {
        this.artistLogo = artistLogo;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getListenFileUrl() {
        return listenFileUrl;
    }

    public void setListenFileUrl(String listenFileUrl) {
        this.listenFileUrl = listenFileUrl;
    }

    public String getLyricUrl() {
        return lyricUrl;
    }

    public void setLyricUrl(String lyricUrl) {
        this.lyricUrl = lyricUrl;
    }

    public int getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(int payFlag) {
        this.payFlag = payFlag;
    }

    public boolean isCanFreeListen() {
        return canFreeListen;
    }

    public void setCanFreeListen(boolean canFreeListen) {
        this.canFreeListen = canFreeListen;
    }

    public boolean isCanFreeDownload() {
        return canFreeDownload;
    }

    public void setCanFreeDownload(boolean canFreeDownload) {
        this.canFreeDownload = canFreeDownload;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public void setAlbumCoverUrl(String albumCoverUrl) {
        this.albumCoverUrl = albumCoverUrl;
    }

    public MusicProvider getSupplier() {
        return supplier;
    }

    public void setSupplier(MusicProvider supplier) {
        this.supplier = supplier;
    }

}
