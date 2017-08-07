package com.example.wenhai.listenall.data.online

import android.os.AsyncTask
import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadAlbumDetailCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.LoadCollectDetailCallback
import com.example.wenhai.listenall.data.MusicSource
import com.example.wenhai.listenall.data.MusicSupplier
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.data.bean.Song
import com.example.wenhai.listenall.utils.JsonCallback
import com.example.wenhai.listenall.utils.LogUtil
import com.example.wenhai.listenall.utils.OkHttpUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * 音乐源：虾米音乐
 * Created by Wenhai on 2017/8/4.
 */
internal class XiaMiMusicSource : MusicSource {

    companion object {
        @JvmStatic
        val TAG = "XiaMiMusicSource"
        val BASE_URL = "http://api.xiami.com/web?v=2.0&app_key=1&"
        val SUFFIX_COLLECT_DETAIL = "&callback=jsonp122&r=collect/detail"
        val SUFFIX_ALBUM_DETAIL = "&page=1&limit=20&callback=jsonp217&r=album/detail"
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
        val url = "http://www.xiami.com/music/newalbum?spm=a1z1s.3057849.6850157.3.mrB6FL&type=oumei"
        LoadNewAlbumTask(count, callback).execute(url)
    }

    override fun loadCollectDetail(id: Long, callback: LoadCollectDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_COLLECT_DETAIL
        OkHttpUtil.getForXiami(url, object : JsonCallback {
            override fun onStart() {
                LogUtil.d(TAG, "开始网络请求")
            }

            override fun onResponse(data: JSONObject) {
                val collect = getCollectFormJson(data)
                callback.onSuccess(collect)
            }

            override fun onFailure(msg: String) {
                LogUtil.e(TAG, msg)
                callback.onFailure()
            }

        })

    }

    fun getCollectFormJson(data: JSONObject): Collect {
        val collect = Collect()
        collect.source = MusicSupplier.XIAMI
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
            song.artistName = jsonSong.getString("singers")
//            song.artistLogo = jsonSong.getString("artist_logo")
//            song.length = jsonSong.getInt("length")
            try {
                song.listenFileUrl = jsonSong.getString("listen_file")
            } catch (e: JSONException) {
                LogUtil.e(TAG, e.localizedMessage)
                song.listenFileUrl = ""
            }
//            song.payFlag = jsonSong.getInt("need_pay_flag")
            try {
                song.lyricUrl = jsonSong.getString("lyric")
            } catch (e: JSONException) {
                song.lyricUrl = ""
                LogUtil.e(TAG, e.localizedMessage)
            }
            song.supplier = MusicSupplier.XIAMI
            songList.add(song)
        }
        return songList
    }

    override fun loadAlbumDetail(id: Long, callback: LoadAlbumDetailCallback) {
        val url = BASE_URL + "id=$id" + SUFFIX_ALBUM_DETAIL
        OkHttpUtil.getForXiami(url, object : JsonCallback {
            override fun onStart() {

            }

            override fun onResponse(data: JSONObject) {
                LogUtil.d(TAG, data.toString())
                val album = Album()
                album.supplier = MusicSupplier.XIAMI
                album.id = data.getLong("album_id")
                album.artist = data.getString("artist_name")
                album.artistId = data.getLong("artist_id")
                album.title = data.getString("album_name")
                album.songNumber = data.getInt("song_count")
                album.publishDate = data.getLong("gmt_publish")
                album.coverUrl = data.getString("album_logo")
                album.songs = getSongsFromJson(data.getJSONArray("songs"))
                callback.onSuccess(album)
            }

            override fun onFailure(msg: String) {
                callback.onFailure()
                LogUtil.e(TAG, msg)
            }

        })
    }

    internal class LoadBannerTask(val callback: LoadBannerCallback)
        : AsyncTask<String, Void, List<String>?>() {

        override fun doInBackground(vararg url: String?): List<String>? {
            val document: Document? = Jsoup.connect(url[0]).get()
            if (document != null) {
                val slider: Element? = document.getElementById("slider")
                val items: Elements = slider !!.getElementsByClass("item")
                val imgUrlList = ArrayList<String>(items.size)
                for (i in 0..items.size - 1) {
                    val imgUrl = items[i].select("a").first().select("img").first().attr("src")
                    imgUrlList.add(imgUrl)
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
                    collect.source = MusicSupplier.XIAMI
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
                    val id = onclick.substring(onclick.indexOf('(', 0, false) + 1, onclick.indexOf('(', 0, false) + 11)
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