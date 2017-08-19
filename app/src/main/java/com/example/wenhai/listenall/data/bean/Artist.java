package com.example.wenhai.listenall.data.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    private String artistId;
    private String name;
    private String desc;
    private String miniImgUrl;
    private String imgUrl;
    private String homePageSuffix;//用于拼接url
    private String hotSongSuffix;//用于拼接url
    private String albumSuffix;//用于拼接url

    public Artist() {
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiniImgUrl() {
        return miniImgUrl;
    }

    public void setMiniImgUrl(String miniImgUrl) {
        this.miniImgUrl = miniImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHomePageSuffix() {
        return homePageSuffix;
    }

    public void setHomePageSuffix(String homePageSuffix) {
        this.homePageSuffix = homePageSuffix;
    }

    public String getHotSongSuffix() {
        return hotSongSuffix;
    }

    public void setHotSongSuffix(String hotSongSuffix) {
        this.hotSongSuffix = hotSongSuffix;
    }

    public String getAlbumSuffix() {
        return albumSuffix;
    }

    public void setAlbumSuffix(String albumSuffix) {
        this.albumSuffix = albumSuffix;
    }

    protected Artist(Parcel in) {
        artistId = in.readString();
        name = in.readString();
        desc = in.readString();
        miniImgUrl = in.readString();
        imgUrl = in.readString();
        homePageSuffix = in.readString();
        hotSongSuffix = in.readString();
        albumSuffix = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistId);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(miniImgUrl);
        dest.writeString(imgUrl);
        dest.writeString(homePageSuffix);
        dest.writeString(hotSongSuffix);
        dest.writeString(albumSuffix);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
