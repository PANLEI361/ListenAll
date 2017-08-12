package com.example.wenhai.listenall.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.wenhai.listenall.data.MusicProvider;


public final class Song implements Parcelable {
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
    private String miniAlbumCoverUrl;
    private MusicProvider supplier;

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(songId);
        dest.writeString(name);
        dest.writeInt(length);
        dest.writeString(listenFileUrl);
        dest.writeString(lyricUrl);
        dest.writeInt(payFlag);
        dest.writeInt(canFreeListen ? 1 : 0);
        dest.writeInt(canFreeDownload ? 1 : 0);
        dest.writeString(artistName);
        dest.writeString(artistLogo);
        dest.writeLong(artistId);
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeString(albumCoverUrl);
        dest.writeString(miniAlbumCoverUrl);
        switch (supplier) {
            case XIAMI:
                dest.writeInt(0);
                break;
            case QQMUSIC:
                dest.writeInt(1);
                break;
            case NETEASE:
                dest.writeInt(2);
                break;
        }

    }

    public Song() {

    }

    protected Song(Parcel in) {
        songId = in.readLong();
        name = in.readString();
        length = in.readInt();
        listenFileUrl = in.readString();
        lyricUrl = in.readString();
        payFlag = in.readInt();
        canFreeListen = in.readInt() == 0;
        canFreeDownload = in.readInt() == 0;
        artistName = in.readString();
        artistLogo = in.readString();
        artistId = in.readLong();
        albumId = in.readLong();
        albumName = in.readString();
        albumCoverUrl = in.readString();
        miniAlbumCoverUrl = in.readString();
        switch (in.readInt()) {
            case 0:
                supplier = MusicProvider.XIAMI;
                break;
            case 1:
                supplier = MusicProvider.QQMUSIC;
                break;
            case 2:
                supplier = MusicProvider.NETEASE;
                break;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    public String getMiniAlbumCoverUrl() {
        return miniAlbumCoverUrl;
    }

    public void setMiniAlbumCoverUrl(String miniAlbumCoverUrl) {
        this.miniAlbumCoverUrl = miniAlbumCoverUrl;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", listenFileUrl='" + listenFileUrl + '\'' +
                ", lyricUrl='" + lyricUrl + '\'' +
                ", payFlag=" + payFlag +
                ", canFreeListen=" + canFreeListen +
                ", canFreeDownload=" + canFreeDownload +
                ", artistName='" + artistName + '\'' +
                ", artistLogo='" + artistLogo + '\'' +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", albumCoverUrl='" + albumCoverUrl + '\'' +
                ", miniAlbumCoverUrl='" + miniAlbumCoverUrl + '\'' +
                ", supplier=" + supplier +
                '}';
    }
}
