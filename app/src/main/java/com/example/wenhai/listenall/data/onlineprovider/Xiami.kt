package com.example.wenhai.listenall.data.onlineprovider

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.text.TextUtils
import com.example.wenhai.listenall.R
import com.example.wenhai.listenall.data.ArtistRegion
import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadAlbumDetailCallback
import com.example.wenhai.listenall.data.LoadArtistAlbumsCallback
import com.example.wenhai.listenall.data.LoadArtistDetailCallback
import com.example.wenhai.listenall.data.LoadArtistHotSongsCallback
import com.example.wenhai.listenall.data.LoadArtistsCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectByCategoryCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.LoadCollectDetailCallback
import com.example.wenhai.listenall.data.LoadRankingCallback
import com.example.wenhai.listenall.data.LoadSearchRecommendCallback
import com.example.wenhai.listenall.data.LoadSearchResultCallback
import com.example.wenhai.listenall.data.LoadSingleRankingCallback
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicSource
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Artist
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.moudle.ranking.RankingContract
import com.example.wenhai.listenall.utils.BaseResponseCallback
import com.example.wenhai.listenall.utils.LogUtil
import com.example.wenhai.listenall.utils.OkHttpUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * 虾米音乐
 * Created by Wenhai on 2017/8/4.
 */
internal class Xiami : MusicSource {
    companion object {
        @JvmStatic
        val TAG = "Xiami"
        val BASE_URL = "http://api.xiami.com/web?v=2.0&app_key=1&"
        val SUFFIX_COLLECT_DETAIL = "&callback=jsonp122&r=collect/detail"
        val SUFFIX_ALBUM_DETAIL = "&page=1&limit=20&callback=jsonp217&r=album/detail"

        val PREFIX_SEARCH_SONG = "http://api.xiami.com/web?v=2.0&app_key=1&key="
        val SUFFIX_SEARCH_SONG = "&page=1&limit=50&callback=jsonp154&r=search/songs"
        val PREFIX_SEARCH_RECOMMEND = "http://www.xiami.com/ajax/search-index?key="

        val INFIX_SEARCH_RECOMMEND = "&_="//后面加时间


        //get hidden listen url when "listen file" is null
        val PREFIX_SONG_DETAIL = "/song/playlist/id/"
        val SUFFIX_SONG_DETAIL = "/object_name/default/object_id/0/cat/json"

        //singer type:0-全部 1-华语 2-欧美 3-日本 4-韩国
        //http://www.xiami.com/artist/index/c/2/type/1
        // c 1-本周流行 2-热门艺人
//        http://www.xiami.com/artist/index/c/2/type/1/class/0/page/1
        val URL_PREFIX_LOAD_ARTISTS = "/artist/index/c/2/type/"
        val URL_INFIX_LOAD_ARTISTS = "/class/0/page/"
        val URL_HOME = "http://www.xiami.com"
        val CATEGORY_HOT_COLLECT = "热门歌单"
        //        val URL_SEARCH_ARTIST = "/search/artist?key=%E7%94%B0"
        val URL_HOT_COLLECT = "/collect/recommend/page/"
        //type:all-全部 huayu-华语 oumei-欧美 ri-日本 han-韩国
        val URL_NEW_ALBUM = "/music/newalbum/type/all/page/"

        //虾米音乐榜
        val URL_RANKING_DATA_MUSIC = "/chart/data?c=103&type=0&page=1&limit=100&_="
        //虾米原创榜
        val URL_RANKING_DATA_ORIGINAL = "/chart/data?c=104&type=0&page=1&limit=100&_="
        //虾米新歌榜
        val URL_RANKING_DATA_NEW = "/chart/data?c=102&type=0&page=1&limit=100&_="
        //billboard
        val URL_RANKING_BILLBOARD = "/chart/data?c=204&type=0&page=1&limit=100&_="
        //uk
        val URL_RANKING_UK = "/chart/data?c=203&type=0&page=1&limit=100&_="
        //oricon
        val URL_RANKING_ORICON = "/chart/data?c=205&type=0&page=1&limit=100&_="

        val RANKING_MUSIC = "虾米音乐榜"
        val RANKING_ORIGIN = "虾米原创榜"
        val RANKING_NEW = "虾米新歌榜"
    }


    override fun loadBanner(callback: LoadBannerCallback) {
        val url = URL_HOME
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                val document = Jsoup.parse(html)
                val slider: Element? = document.getElementById("slider")
                val items: Elements = slider !!.getElementsByClass("item")
                val imgUrlList = ArrayList<String>(items.size)
                (0 until items.size).mapTo(imgUrlList) {
                    items[it].select("a").first().select("img").first().attr("src")
                }
                callback.onSuccess(imgUrlList)
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

        })
    }

    override fun loadHotCollect(page: Int, callback: LoadCollectCallback) {
        val url = URL_HOME + URL_HOT_COLLECT + page
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                val document = Jsoup.parse(html)
                try {
                    val pageElement = document.getElementById("page")
                    val list = pageElement.getElementsByClass("block_items clearfix")
                    val collectList = ArrayList<Collect>(6)
                    for (i in 0 until list.size) {
                        val element = list[i]
                        val a = element.select("a").first()
                        val title = a.attr("title")
                        val ref = a.attr("href")
                        val id = parseIdFromHref(ref)
                        val coverUrl = a.select("img").first().attr("src")
                        val collect = Collect()
                        collect.id = id.toLong()
                        collect.title = title
                        collect.coverUrl = coverUrl.substring(0, coverUrl.length - 11)
                        collect.source = MusicProvider.XIAMI
                        collectList.add(collect)
                    }
                    callback.onSuccess(collectList)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多歌单了")
                }

            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }
        })

    }

    override fun loadNewAlbum(page: Int, callback: LoadAlbumCallback) {
        val url = URL_HOME + URL_NEW_ALBUM + page
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                try {
                    parseNewAlbums(html, callback)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多音乐了")
                }
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }
        })

    }

    private fun parseNewAlbums(html: String, callback: LoadAlbumCallback) {
        val document = Jsoup.parse(html)
        val albumElement = document.getElementById("albums")
        val albums = albumElement.getElementsByClass("album")
        val albumList = ArrayList<Album>()
        if (albums.size > 0) {
            for (i in 0 until albums.size) {
                val imgElement = albums[i].getElementsByClass("image").first()
                val onclick: String = imgElement.select("b").first().attr("onclick")
                val id = onclick.substring(onclick.indexOf('(', 0, false) + 1, onclick.indexOf(',', 0, false))
                val coverUrl = imgElement.select("img").first().attr("src")
                val title = albums[i].getElementsByClass("info").first()
                        .select("p").first()
                        .select("a").first().attr("title")
                val artist = albums[i].getElementsByClass("info").first()
                        .select("p").next()
                        .select("a").first().attr("title")
                val album = Album()
                album.coverUrl = coverUrl.substring(0, coverUrl.length - 20)
                album.title = title
                album.artist = artist
                album.id = id.toLong()
                albumList.add(album)
            }
            callback.onSuccess(albumList)
        } else {
            callback.onFailure("")
        }
    }

    override fun loadCollectDetail(id: Long, callback: LoadCollectDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_COLLECT_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                val collect = parseCollectFormJson(jsonObject)
                callback.onSuccess(collect)
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }

        })

    }

    private fun parseCollectFormJson(data: JSONObject): Collect {
        val collect = Collect()
        collect.source = MusicProvider.XIAMI
        collect.id = data.getLong("list_id")
        collect.title = data.getString("collect_name")
        collect.coverUrl = data.getString("logo")
        collect.songCount = data.getInt("songs_count")
        collect.createDate = data.getLong("gmt_create")
        collect.updateDate = data.getLong("gmt_modify")
        collect.playTimes = data.getInt("play_count")
        collect.songs = parseSongsFromJson(data.getJSONArray("songs"))
        return collect
    }

    private fun parseSongsFromJson(songs: JSONArray?): ArrayList<Song>? {
        val songCount = songs !!.length()
        val songList = ArrayList<Song>(songCount)
        for (i in 0 until songCount) {
            val song = Song()
            val jsonSong: JSONObject = songs.get(i) as JSONObject
            song.songId = jsonSong.getLong("song_id")
            song.name = jsonSong.getString("song_name")
            song.albumId = jsonSong.getLong("album_id")
            song.albumName = jsonSong.getString("album_name")
            song.artistId = jsonSong.getLong("artist_id")
            try {
                song.artistName = jsonSong.getString("artist_name")
            } catch (e: JSONException) {
                song.artistName = jsonSong.getString("singers")
            }
            // multi artist
            if (song.artistName.contains(";")) {
                val artists = song.artistName.split(";")
                val artistBuilder = StringBuilder()
                for (artist in artists) {
                    artistBuilder.append(artist)
                    artistBuilder.append("&")
                }
                song.artistName = artistBuilder.substring(0, artistBuilder.length - 1)
            }

            song.listenFileUrl = ""
            song.supplier = MusicProvider.XIAMI
            songList.add(song)
        }
        return songList
    }

    override fun loadSongDetail(song: Song, callback: LoadSongDetailCallback) {
        val id = song.songId
        val url = URL_HOME + PREFIX_SONG_DETAIL + id + SUFFIX_SONG_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                val songInfo = jsonObject.getJSONObject("data")
                if (songInfo.isNull("trackList")) {
                    //不能播放
                    callback.onFailure("当前歌曲不能播放，请切换平台试试")
                } else {
                    val trackList: JSONArray = songInfo.getJSONArray("trackList")
                    if (trackList.length() > 0) {
                        val track = trackList.getJSONObject(0)
                        if (TextUtils.isEmpty(song.listenFileUrl)) {
                            song.listenFileUrl = getListenUrlFromLocation(track.getString("location"))
                        }
                        val canFreeListen = track.getJSONObject("purviews").getJSONObject("LISTEN").getString("LOW")
                        song.isCanFreeListen = canFreeListen == "FREE"
                        val canFreeDownload = track.getJSONObject("purviews").getJSONObject("DOWNLOAD").getString("LOW")
                        song.isCanFreeDownload = canFreeDownload == "FREE"
                        song.length = track.getInt("length")
                        try {
                            song.lyricUrl = track.getString("lyric_url")
                        } catch (e: JSONException) {
                            song.lyricUrl = ""
                        }
                        song.albumCoverUrl = track.getString("album_pic")
                        song.miniAlbumCoverUrl = track.getString("pic")
                        song.albumName = track.getString("album_name")
                        song.albumId = track.getLong("album_id")
                        song.artistName = track.getString("artist")
                        song.artistId = track.getLong("artist_id")
                        LogUtil.d(TAG, "artistId=${song.artistId}")
                        callback.onSuccess(song)
                    } else {
                        callback.onFailure("当前歌曲不能播放，请切换平台试试")
                    }

                }
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }

        })
    }

    /*
     *parse "location" string and get listen file url
     */
    private fun getListenUrlFromLocation(location: String): String {
        val num = location[0] - '0'
        val avgLen = Math.floor((location.substring(1).length / num).toDouble()).toInt()
        val remainder = location.substring(1).length % num

        val result = ArrayList<String>()
        (0 until remainder).mapTo(result) { location.substring(it * (avgLen + 1) + 1, (it + 1) * (avgLen + 1) + 1) }
        (0 until num - remainder).mapTo(result) { location.substring((avgLen + 1) * remainder).substring(it * avgLen + 1, (it + 1) * avgLen + 1) }

        val s = ArrayList<String>()
        for (i in 0 until avgLen) {
            (0 until num).mapTo(s) { result[it][i].toString() }
        }
        (0 until remainder).mapTo(s) { result[it][result[it].length - 1].toString() }

        val joinStr = s.joinToString("")
        return URLDecoder.decode(joinStr, "utf-8").replace("^", "0")
    }

    override fun loadAlbumDetail(id: Long, callback: LoadAlbumDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_ALBUM_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                super.onJsonObjectResponse(jsonObject)
                val album = Album()
                album.supplier = MusicProvider.XIAMI
                album.id = jsonObject.getLong("album_id")
                album.artist = jsonObject.getString("artist_name")
                album.artistId = jsonObject.getLong("artist_id")
                album.title = jsonObject.getString("album_name")
                album.songNumber = jsonObject.getInt("song_count")
                album.publishDate = jsonObject.getLong("gmt_publish")
                album.coverUrl = jsonObject.getString("album_logo")
                album.miniCoverUrl = album.coverUrl + "@1e_1c_100Q_100w_100h"
                album.songs = parseSongsFromJson(jsonObject.getJSONArray("songs"))
                callback.onSuccess(album)
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }

        })
    }

    override fun searchByKeyword(keyword: String, callback: LoadSearchResultCallback) {
        val encodedKeyword = URLEncoder.encode(keyword, "utf-8")
        val url = PREFIX_SEARCH_SONG + encodedKeyword + SUFFIX_SEARCH_SONG
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                super.onJsonObjectResponse(jsonObject)
                val songs: ArrayList<Song>? = parseSongsFromJson(jsonObject.getJSONArray("songs"))
                if (songs == null || songs.size == 0) {
                    callback.onFailure("搜索失败")
                } else {
                    callback.onSuccess(songs)
                }
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }

        })
    }

    override fun loadSearchRecommend(keyword: String, callback: LoadSearchRecommendCallback) {
        val currentTime = System.currentTimeMillis()
        val url = PREFIX_SEARCH_RECOMMEND + URLEncoder.encode(keyword, "utf-8") + INFIX_SEARCH_RECOMMEND + currentTime
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {

            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                val keywordList = parseRecommendKeywords(html)
                callback.onSuccess(keywordList)
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

        })

    }

    private fun parseRecommendKeywords(string: String): List<String> {
        val keywordList = ArrayList<String>()
        val document = Jsoup.parse(string)
        val result = document.getElementsByClass("result")
        result.map { it.select("a").first().attr("title") }
                .filterNotTo(keywordList) { TextUtils.isEmpty(it) }
        return keywordList
    }

    override fun loadArtists(region: ArtistRegion, page: Int, callback: LoadArtistsCallback) {
        val type = when (region) {
            ArtistRegion.ALL -> {
                0
            }
            ArtistRegion.CN -> {
                1
            }
            ArtistRegion.EA -> {
                2
            }
            ArtistRegion.JP -> {
                3
            }
            ArtistRegion.KO -> {
                4
            }
        }
        val url = URL_HOME + URL_PREFIX_LOAD_ARTISTS + "$type" + URL_INFIX_LOAD_ARTISTS + page
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

            override fun onHtmlResponse(html: String) {
                super.onHtmlResponse(html)
                try {
                    val artists: ArrayList<Artist> = parseArtistList(html)
                    callback.onSuccess(artists)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多艺人了")
                }
            }

        })

    }

    private fun parseArtistList(html: String): ArrayList<Artist> {
        val result = ArrayList<Artist>()
        val document = Jsoup.parse(html)
        val artists = document.getElementById("artists")
        val artistElements = artists.getElementsByClass("artist")
        for (artistElement in artistElements) {
            val artist = Artist()
            val img = artistElement.getElementsByClass("image").first()
            artist.name = artistElement.getElementsByClass("info").first()
                    .select("a").first().attr("title")
            artist.miniImgUrl = img.select("img").first()
                    .attr("src")
            val homePageSuffix = artistElement.getElementsByClass("image").first()
                    .select("a").first()
                    .attr("href")
            artist.homePageSuffix = homePageSuffix
            val artistId = homePageSuffix.substring(homePageSuffix.lastIndexOf("/") + 1)
            artist.artistId = artistId
            artist.hotSongSuffix = "/artist/top-" + artistId
            artist.albumSuffix = "/artist/album-" + artistId
            result.add(artist)
        }
        return result

    }

    override fun loadArtistDetail(artist: Artist, callback: LoadArtistDetailCallback) {
        val url = URL_HOME + artist.homePageSuffix
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                super.onHtmlResponse(html)
                val detailedArtist = parseAndAddArtistDetail(html, artist)
                callback.onSuccess(detailedArtist)
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

        })
    }

    //get desc and imgUrl
    private fun parseAndAddArtistDetail(html: String, artist: Artist): Artist {
        val document = Jsoup.parse(html)
        val block = document.getElementById("artist_block")
        val info = block.getElementById("artist_info")
        val desc = info.select("tr").last()
                .getElementsByClass("record").first()
                .text()
        artist.desc = desc
        val img = block.getElementById("artist_photo")
        val imgUrl = img.select("a").first().attr("href")
        artist.imgUrl = imgUrl
        return artist
    }

    override fun loadArtistHotSongs(artist: Artist, page: Int, callback: LoadArtistHotSongsCallback) {
        val url = URL_HOME + artist.hotSongSuffix + "?page=$page"
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                super.onHtmlResponse(html)
                try {
                    val hotSongs = parseArtistHotSongs(artist, html)
                    callback.onSuccess(hotSongs)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多歌曲了")
                }
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

        })
    }

    private fun parseArtistHotSongs(artist: Artist, html: String): List<Song> {
        val document = Jsoup.parse(html)
        val songs = ArrayList<Song>()
        val trackList = document.getElementsByClass("track_list").first()
        val tracks = trackList.select("tr")
        for (track in tracks) {
            val song = Song()
            song.name = track.getElementsByClass("song_name").first()
                    .select("a").first()
                    .attr("title")
            val onClick = track.getElementsByClass("song_act").first()
                    .getElementsByClass("song_play").first()
                    .attr("onClick")
            val extra = track.getElementsByClass("song_name").first()
                    .getElementsByClass("show_zhcn").first()
            if (extra != null) {
                //临时显示用
                song.albumName = extra.text()
            } else {
                song.albumName = ""
            }
            val songId = onClick.substring(onClick.indexOf("'") + 1, onClick.indexOf(",") - 1)
            song.songId = songId.toLong()
            song.artistName = artist.name
            song.supplier = MusicProvider.XIAMI
            songs.add(song)
        }
        return songs
    }

    override fun loadArtistAlbums(artist: Artist, page: Int, callback: LoadArtistAlbumsCallback) {
        val url = URL_HOME + artist.albumSuffix + "?page=$page"
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                super.onHtmlResponse(html)
                try {
                    val albums = parseArtistAlbums(html)
                    callback.onSuccess(albums)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多专辑了")
                }
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }

        })
    }

    private fun parseArtistAlbums(html: String): List<Album> {
        val albums = ArrayList<Album>()
        val document = Jsoup.parse(html)
        val albumsElement = document.getElementById("artist_albums").
                getElementsByClass("albumThread_list").first()
                .select("li")
        for (albumElement in albumsElement) {
            val album = Album()
            album.supplier = MusicProvider.XIAMI
            val id = albumElement.select("div").first().attr("id")
            album.id = id.substring(id.indexOf("_") + 1).toLong()
            album.miniCoverUrl = albumElement.getElementsByClass("cover").first()
                    .select("img").attr("src")
            album.coverUrl = album.miniCoverUrl.substring(0, album.miniCoverUrl.indexOf("@"))
            val detail = albumElement.getElementsByClass("detail").first()
            val title = detail.getElementsByClass("name").first()
                    .select("a").first().attr("title")
            album.title = title
            val publishDate = detail.getElementsByClass("company").first()
                    .select("a").last().text()
            album.publishDateStr = publishDate
            albums.add(album)
        }

        return albums
    }

    override fun loadCollectByCategory(category: String, page: Int, callback: LoadCollectByCategoryCallback) {
        val url = if (category == CATEGORY_HOT_COLLECT) {
            "http://www.xiami.com/collect/recommend/page/$page" //热门
        } else {
            "http://www.xiami.com/search/collect/page/$page?key=${URLEncoder.encode(category, "utf-8")}"
        }
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                super.onHtmlResponse(html)
                try {
                    val collects = parseCollectsFromHTML(html)
                    callback.onSuccess(collects)
                } catch (e: NullPointerException) {
                    callback.onFailure("没有更多歌单了")
                }
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure(msg)
            }

        })

    }

    private fun parseCollectsFromHTML(html: String): List<Collect> {
        val document = Jsoup.parse(html)
        val collects = ArrayList<Collect>()
        val page = document.getElementById("page")
        val list = page.getElementsByClass("block_items clearfix")
        for (i in 0 until list.size) {
            val element = list[i]
            val a = element.select("a").first()
            val title = a.attr("title")
            val ref = a.attr("href")
            val id = parseIdFromHref(ref)
            val coverUrl = a.select("img").first().attr("src")
            val collect = Collect()
            collect.id = id.toLong()
            collect.title = title
            collect.coverUrl = coverUrl.substring(0, coverUrl.length - 11)
            collect.source = MusicProvider.XIAMI
            collects.add(collect)
        }
        return collects
    }

    private fun parseIdFromHref(ref: String): Int {
        val idStr = ref.substring(ref.lastIndexOf('/') + 1)
        return Integer.valueOf(idStr) !!
    }

    override fun loadOfficialRanking(provider: MusicProvider, callback: LoadRankingCallback) {
        callback.onStart()
        val collects = ArrayList<Collect>()
        LoadRankingListTask(collects, RANKING_MUSIC, callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_HOME + URL_RANKING_DATA_MUSIC + System.currentTimeMillis())
        LoadRankingListTask(collects, RANKING_ORIGIN, callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_HOME + URL_RANKING_DATA_ORIGINAL + System.currentTimeMillis())
        LoadRankingListTask(collects, RANKING_NEW, callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_HOME + URL_RANKING_DATA_NEW + System.currentTimeMillis())
    }

    override fun loadGlobalRanking(ranking: RankingContract.GlobalRanking, callback: LoadSingleRankingCallback) {
        val collect = Collect()
        collect.source = MusicProvider.XIAMI
        val suffix = when (ranking) {
            RankingContract.GlobalRanking.BILLBOARD -> {
                collect.title = "Billboard周榜"
                collect.desc = "美国公告牌每周最热100首单曲，每周五更新"
                collect.coverDrawable = R.drawable.ranking_billboard
                URL_RANKING_BILLBOARD
            }

            RankingContract.GlobalRanking.UK
            -> {
                collect.coverDrawable = R.drawable.ranking_uk
                collect.title = "英国UK榜"
                collect.desc = "英国官方每周最热单曲排行榜，每周一更新"
                URL_RANKING_UK
            }
            RankingContract.GlobalRanking.ORICON
            -> {
                collect.coverDrawable = R.drawable.ranking_oricon
                collect.title = "日本 Oricon 周榜"
                collect.desc = "日本公信榜上周销量前20位单曲，每周三更新"
                URL_RANKING_ORICON
            }
        }
        val url = URL_HOME + suffix + System.currentTimeMillis()
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                callback.onStart()
            }

            override fun onHtmlResponse(html: String) {
                collect.songs = parseRankingSongs(Jsoup.parse(html))
                callback.onSuccess(collect)
            }

            override fun onFailure(msg: String) {
                callback.onFailure(msg)
            }
        })

    }

    private fun parseRankingSongs(document: Document): ArrayList<Song>? {
        val songElements = document.getElementsByClass("song")
        val moreElements = document.getElementsByClass("more")

        val songs = ArrayList<Song>()
        for (i in 0 until songElements.size) {
            val element = songElements[i]
            val song = Song()
            song.supplier = MusicProvider.XIAMI
            song.miniAlbumCoverUrl = element.getElementsByClass("image").first()
                    .select("img").first().attr("src")
            song.albumCoverUrl = song.miniAlbumCoverUrl.substring(0, song.miniAlbumCoverUrl.indexOf("@"))
            val info = element.getElementsByClass("info").first()
            song.name = info.select("p").first().select("a").first().text()
            song.artistName = info.select("p").last().select("a").first().text()
            val songId = moreElements[i].select("li").first().attr("onclick")
            song.songId = songId.substring(songId.indexOf("(") + 1, songId.indexOf(",")).toLong()
            songs.add(song)
        }
        return songs
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadRankingListTask(val collects: ArrayList<Collect>, private val rankingTitle: String, private val callback: LoadRankingCallback)
        : AsyncTask<String, Void, Collect>() {

        override fun doInBackground(vararg urls: String?): Collect? {
            val url = urls[0]

            try {
                val document = Jsoup.connect(url).get()
                val collect = Collect()
                collect.title = rankingTitle
                when (rankingTitle) {
                    RANKING_MUSIC -> {
                        collect.coverDrawable = R.mipmap.xiami_music
                        collect.desc = "虾米音乐全曲库歌曲试听量排名"
                    }
                    RANKING_NEW -> {
                        collect.coverDrawable = R.mipmap.xiami_new
                        collect.desc = "虾米音乐30天内新歌试听量排名"
                    }
                    RANKING_ORIGIN -> {
                        collect.coverDrawable = R.mipmap.xiami_original
                        collect.desc = "虾米音乐人最新作品试听量排名"
                    }
                }
                collect.source = MusicProvider.XIAMI
                collect.songs = parseRankingSongs(document)
                return collect
            } catch (e: IOException) {
                callback.onFailure("获取排行榜信息失败")
            }
            return null
        }

        override fun onPostExecute(result: Collect?) {
            super.onPostExecute(result)
            if (result != null) {
                collects.add(result)
                if (collects.size == 3) {
                    callback.onSuccess(collects)
                }
            }
        }


    }
}