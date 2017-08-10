package com.example.wenhai.listenall.data.onlineprovider

import android.os.AsyncTask
import android.text.TextUtils
import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadAlbumDetailCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.LoadCollectDetailCallback
import com.example.wenhai.listenall.data.LoadSearchRecommendCallback
import com.example.wenhai.listenall.data.LoadSearchResultCallback
import com.example.wenhai.listenall.data.LoadSongDetailCallback
import com.example.wenhai.listenall.data.MusicProvider
import com.example.wenhai.listenall.data.MusicSource
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song
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
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Calendar

/**
 * 音乐源：虾米音乐
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
        //        http://www.xiami.com/ajax/search-index?key=%E6%88%91&_=1502344376948
        val PREFIX_SEARCH_RECOMMEND = "http://www.xiami.com/ajax/search-index?key="
        //后面加时间
        val INFIX_SEARCH_RECOMMEND = "&_="


        //get hidden listen url when "listen file" is null
        val PREFIX_SONG_DETAIL = "http://www.xiami.com/song/playlist/id/"
        val SUFFIX_SONG_DETAIL = "/object_name/default/object_id/0/cat/json"
    }


    override fun loadBanner(callback: LoadBannerCallback) {
        val url = "http://www.xiami.com/"
        LoadBannerTask(callback).execute(url)
    }

    override fun loadHotCollect(count: Int, callback: LoadCollectCallback) {
        val url = "http://www.xiami.com/collect/recommend/page/1"
        LoadCollectTask(count, callback).execute(url)
    }

    override fun loadNewAlbum(count: Int, callback: LoadAlbumCallback) {
//        type contains "all" "huayu" "oumei" "ri" "han"
        //presenting "全部" "华语"  "欧美"  "日本" "韩国"
        val url = "http://www.xiami.com/music/newalbum/type/oumei/page/1"
        LoadNewAlbumTask(count, callback).execute(url)
    }

    override fun loadCollectDetail(id: Long, callback: LoadCollectDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_COLLECT_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
                LogUtil.d(TAG, "开始网络请求")
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                val collect = getCollectFormJson(jsonObject)
                callback.onSuccess(collect)
            }

            override fun onFailure(msg: String) {
                LogUtil.e(TAG, msg)
                callback.onFailure()
            }

        })

    }

    private fun getCollectFormJson(data: JSONObject): Collect {
        val collect = Collect()
        collect.source = MusicProvider.XIAMI
        collect.id = data.getLong("list_id")
        collect.title = data.getString("collect_name")
        collect.coverUrl = data.getString("logo")
        collect.songCount = data.getInt("songs_count")
        collect.createDate = data.getLong("gmt_create")
        collect.playTimes = data.getInt("play_count")
        collect.songs = getSongsFromJson(data.getJSONArray("songs"))
        return collect
    }

    private fun getSongsFromJson(songs: JSONArray?): ArrayList<Song>? {
        val songCount = songs !!.length()
        val songList = ArrayList<Song>(songCount)
        for (i in 0..songCount - 1) {
            val song = Song()
            val jsonSong: JSONObject = songs.get(i) as JSONObject
            song.songId = jsonSong.getLong("song_id")
            song.name = jsonSong.getString("song_name")
            song.albumId = jsonSong.getLong("album_id")
            song.albumName = jsonSong.getString("album_name")
            song.albumCoverUrl = jsonSong.getString("album_logo")
            song.artistId = jsonSong.getLong("artist_id")
            try {
                song.artistName = jsonSong.getString("artist_name")
            } catch (e: JSONException) {
                song.artistName = jsonSong.getString("singers")
            }
            try {
                song.artistLogo = jsonSong.getString("artist_logo")
            } catch (e: JSONException) {
                song.artistLogo = ""
            }
            try {
                song.length = jsonSong.getInt("length")
            } catch (e: JSONException) {
                song.length = 0
            }
            try {
                song.listenFileUrl = jsonSong.getString("listen_file")
            } catch (e: JSONException) {
                song.listenFileUrl = ""
            }
//            song.payFlag = jsonSong.getInt("need_pay_flag")
            try {
                song.lyricUrl = jsonSong.getString("lyric")
            } catch (e: JSONException) {
                song.lyricUrl = ""
            }
            song.supplier = MusicProvider.XIAMI
            songList.add(song)
        }
        return songList
    }


    override fun loadSongDetail(song: Song, callback: LoadSongDetailCallback) {
        val id = song.songId
        val url = PREFIX_SONG_DETAIL + id + SUFFIX_SONG_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                val songInfo = jsonObject.getJSONObject("data")
                val trackList: JSONArray = songInfo.getJSONArray("trackList")
                if (trackList.length() > 0) {
                    val track = trackList.getJSONObject(0)
                    song.listenFileUrl = getListenUrlFromLocation(track.getString("location"))
                    val canFreeListen = track.getJSONObject("purviews").getJSONObject("LISTEN").getString("LOW")
                    song.isCanFreeListen = canFreeListen == "FREE"
                    val canFreeDownload = track.getJSONObject("purviews").getJSONObject("DOWNLOAD").getString("LOW")
                    song.isCanFreeDownload = canFreeDownload == "FREE"
                    song.length = track.getString("length").toInt()
                    try {
                        song.lyricUrl = track.getString("lyric_url")
                    } catch (e: JSONException) {
                        song.lyricUrl = ""
                    }
                    callback.onSuccess(song)

                } else {
                    callback.onFailure()
                }

            }

            override fun onFailure(msg: String) {
                callback.onFailure()
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
        (0..remainder - 1).mapTo(result) { location.substring(it * (avgLen + 1) + 1, (it + 1) * (avgLen + 1) + 1) }
        (0..num - remainder - 1).mapTo(result) { location.substring((avgLen + 1) * remainder).substring(it * avgLen + 1, (it + 1) * avgLen + 1) }

        val s = ArrayList<String>()
        for (i in 0..avgLen - 1) {
            (0..num - 1).mapTo(s) { result[it][i].toString() }
        }
        (0..remainder - 1).mapTo(s) { result[it][result[it].length - 1].toString() }

        val joinStr = s.joinToString("")
        val listenFile = URLDecoder.decode(joinStr, "utf-8").replace("^", "0")
        return listenFile
    }

    override fun loadAlbumDetail(id: Long, callback: LoadAlbumDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_ALBUM_DETAIL
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {

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
                album.songs = getSongsFromJson(jsonObject.getJSONArray("songs"))
                callback.onSuccess(album)
            }

            override fun onFailure(msg: String) {
                callback.onFailure()
                LogUtil.e(TAG, msg)
            }

        })
    }

    override fun searchByKeyword(keyword: String, callback: LoadSearchResultCallback) {
        val encodedKeyword = URLEncoder.encode(keyword, "utf-8")
        val url = PREFIX_SEARCH_SONG + encodedKeyword + SUFFIX_SEARCH_SONG
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {
            override fun onStart() {
            }

            override fun onJsonObjectResponse(jsonObject: JSONObject) {
                super.onJsonObjectResponse(jsonObject)
                val songs: ArrayList<Song>? = getSongsFromJson(jsonObject.getJSONArray("songs"))
                if (songs == null || songs.size == 0) {
                    callback.onFailure()
                } else {
                    callback.onSuccess(songs)
                }
            }

            override fun onFailure(msg: String) {
                callback.onFailure()
            }

        })
    }

    override fun loadSearchRecommend(keyword: String, callback: LoadSearchRecommendCallback) {
        val currentTime = Calendar.getInstance().timeInMillis
        val url = PREFIX_SEARCH_RECOMMEND + URLEncoder.encode(keyword, "utf-8") + INFIX_SEARCH_RECOMMEND + currentTime
        OkHttpUtil.getForXiami(url, object : BaseResponseCallback() {

            override fun onStart() {
                super.onStart()
            }

            override fun onStringResponse(string: String) {
                val keywordList = getRecommendKeywords(string)
                callback.onSuccess(keywordList)
            }

            override fun onFailure(msg: String) {
                super.onFailure(msg)
                callback.onFailure()
            }

        })

    }

    private fun getRecommendKeywords(string: String): List<String> {
        val keywordList = ArrayList<String>()
        val document = Jsoup.parse(string)
        val result = document.getElementsByClass("result")
        result.map { it.select("a").first().attr("title") }
                .filterNotTo(keywordList) { TextUtils.isEmpty(it) }
        return keywordList
    }

    internal class LoadBannerTask(val callback: LoadBannerCallback)
        : AsyncTask<String, Void, List<String>?>() {

        override fun doInBackground(vararg url: String?): List<String>? {
            val document: Document? = Jsoup.connect(url[0]).get()
            if (document != null) {
                val slider: Element? = document.getElementById("slider")
                val items: Elements = slider !!.getElementsByClass("item")
                val imgUrlList = ArrayList<String>(items.size)
                (0..items.size - 1).mapTo(imgUrlList) {
                    items[it].select("a").first().select("img").first().attr("src")
                    //                    val ref = items[i].select("a").first().attr("href")
                }

                return imgUrlList
            } else {
                return null
            }
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            if (result == null || result.isEmpty()) {
                callback.onFailure()
            } else {
                callback.onSuccess(result)
            }
        }

    }

    internal class LoadCollectTask(val count: Int, val callback: LoadCollectCallback)
        : AsyncTask<String, Void, List<Collect>?>() {
        override fun doInBackground(vararg url: String?): List<Collect>? {
            val document: Document? = Jsoup.connect("http://www.xiami.com/collect/recommend/page/1").get()
            if (document != null) {
                val page = document.getElementById("page")
                val list = page.getElementsByClass("block_items clearfix")
                val collectList = ArrayList<Collect>(6)
                for (i in 0..count - 1) {
                    val element = list[i]
                    val a = element.select("a").first()
                    val title = a.attr("title")
                    val ref = a.attr("href")
                    val id = getIdFromHref(ref)
                    val coverUrl = a.select("img").first().attr("src")
                    val collect = Collect()
                    collect.id = id.toLong()
                    collect.title = title
                    collect.coverUrl = coverUrl.substring(0, coverUrl.length - 11)
                    collect.source = MusicProvider.XIAMI
                    collectList.add(collect)
                }
                return collectList
            } else {
                return null
            }
        }

        override fun onPostExecute(result: List<Collect>?) {
            super.onPostExecute(result)
            if (result == null || result.isEmpty()) {
                callback.onFailure()
            } else {
                callback.onSuccess(result)
            }
        }

        private fun getIdFromHref(ref: String): Int {
            val idStr = ref.substring(ref.lastIndexOf('/') + 1)
            return Integer.valueOf(idStr) !!
        }


    }

    internal class LoadNewAlbumTask(val count: Int, val callback: LoadAlbumCallback)
        : AsyncTask<String, Void, List<Album>?>() {
        override fun doInBackground(vararg url: String?): List<Album>? {
            val document = Jsoup.connect(url[0]).get()
            if (document != null) {
                val albumElement = document.getElementById("albums")
                val albums = albumElement.getElementsByClass("album")
                val albumList = ArrayList<Album>(count)
                for (i in 0..count - 1) {
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
                return albumList
            } else {
                return null
            }
        }

        override fun onPostExecute(result: List<Album>?) {
            super.onPostExecute(result)
            if (result == null || result.isEmpty()) {
                callback.onFailure()
            } else {
                callback.onSuccess(result)
            }
        }

    }
}