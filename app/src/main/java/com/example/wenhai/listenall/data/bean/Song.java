package com.example.wenhai.listenall.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.example.wenhai.listenall.data.MusicProvider;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public final class Song implements Parcelable, Serializable {
    @Id
    private Long id;

    private static final long serialVersionUID = 10001;
    private long songId;
    private String name;
    private int length;//second
    private String listenFileUrl;//xiami
    private String lyricUrl;//lyric
    private String artistName;
    private String displayArtistName;
    private String artistLogo;
    private long artistId;
    private long albumId;
    private String albumName;
    private String albumCoverUrl;
    private String miniAlbumCoverUrl;
    @Transient
    private MusicProvider supplier;
    private String providerName;
//    private boolean isDownload;

    private boolean isPlaying = false;

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(songId);
        dest.writeString(name);
        dest.writeInt(length);
        dest.writeString(listenFileUrl);
        dest.writeString(lyricUrl);
        dest.writeString(artistName);
        dest.writeString(displayArtistName);
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
        dest.writeInt(isPlaying ? 1 : 0);

    }

    public Song() {

    }

    protected Song(Parcel in) {
        songId = in.readLong();
        name = in.readString();
        length = in.readInt();
        listenFileUrl = in.readString();
        lyricUrl = in.readString();
        artistName = in.readString();
        displayArtistName = in.readString();
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
        isPlaying = in.readInt() == 1;

    }

    @Generated(hash = 1874810466)
    public Song(Long id, long songId, String name, int length, String listenFileUrl,
                String lyricUrl, String artistName, String displayArtistName, String artistLogo,
                long artistId, long albumId, String albumName, String albumCoverUrl,
                String miniAlbumCoverUrl, String providerName, boolean isPlaying) {
        this.id = id;
        this.songId = songId;
        this.name = name;
        this.length = length;
        this.listenFileUrl = listenFileUrl;
        this.lyricUrl = lyricUrl;
        this.artistName = artistName;
        this.displayArtistName = displayArtistName;
        this.artistLogo = artistLogo;
        this.artistId = artistId;
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumCoverUrl = albumCoverUrl;
        this.miniAlbumCoverUrl = miniAlbumCoverUrl;
        this.providerName = providerName;
        this.isPlaying = isPlaying;
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

    @Keep
    public void setArtistName(String artistName) {
        this.artistName = artistName;
        String[] split = artistName.split(";");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
            sb.append("/");
        }
        setDisplayArtistName(sb.toString().substring(0, sb.length() - 1));
    }

    public String getDisplayArtistName() {
        return displayArtistName;
    }

    public void setDisplayArtistName(String displayArtistName) {
        this.displayArtistName = displayArtistName;
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
        this.providerName = supplier.name();
    }

    public String getMiniAlbumCoverUrl() {
        return miniAlbumCoverUrl;
    }

    public void setMiniAlbumCoverUrl(String miniAlbumCoverUrl) {
        this.miniAlbumCoverUrl = miniAlbumCoverUrl;
    }

    public Artist getArtist() {
        Artist artist = new Artist();
        artist.setArtistId(String.valueOf(artistId));
        artist.setName(artistName);
        return artist;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            Song song = (Song) obj;
            if (song.songId == this.songId && song.getSupplier() == this.supplier) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", listenFileUrl='" + listenFileUrl + '\'' +
                ", lyricUrl='" + lyricUrl + '\'' +
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
