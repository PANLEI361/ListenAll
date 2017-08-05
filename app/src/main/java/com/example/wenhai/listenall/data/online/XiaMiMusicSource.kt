package com.example.wenhai.listenall.data.online

import android.os.AsyncTask
import com.example.wenhai.listenall.data.LoadAlbumCallback
import com.example.wenhai.listenall.data.LoadBannerCallback
import com.example.wenhai.listenall.data.LoadCollectCallback
import com.example.wenhai.listenall.data.MusicSource
import com.example.wenhai.listenall.data.MusicSupplier
import com.example.wenhai.listenall.data.bean.Album
import com.example.wenhai.listenall.data.bean.Collect
import com.example.wenhai.listenall.utils.LogUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * 音乐源：虾米音乐
 * Created by Wenhai on 2017/8/4.
 */
internal class XiaMiMusicSource : MusicSource {


    override fun loadBanner(callback: LoadBannerCallback) {
        val url = "http://www.xiami.com/"
        BannerAsyncTask(callback).execute(url)
    }

    override fun loadHotCollect(count: Int, callback: LoadCollectCallback) {
        val url = "http://www.xiami.com/collect/recommend/page/1"
        LoadCollectTask(count, callback).execute(url)
    }

    override fun loadNewAlbum(count: Int, callback: LoadAlbumCallback) {
        val url = "http://www.xiami.com/music/newalbum?spm=a1z1s.3057849.6850157.3.mrB6FL&type=oumei"
        LoadNewAlbumTask(count, callback).execute(url)
    }
}

class BannerAsyncTask(val callback: LoadBannerCallback)
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
            LogUtil.d(TAG, "count = $count")
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
                LogUtil.d(TAG, "$collect")
                collectList.add(collect)
            }
            LogUtil.d(TAG, "listSize=${collectList.size}")
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

    companion object {
        @JvmStatic
        val TAG = "XiaMiMusicSource"
    }

}

class LoadNewAlbumTask(val count: Int, val callback: LoadAlbumCallback)
    : AsyncTask<String, Void, List<Album>?>() {
    override fun doInBackground(vararg url: String?): List<Album>? {
        val document = Jsoup.connect(url[0]).get()
        if (document != null) {
            val albumElement = document.getElementById("albums")
            val albums = albumElement.getElementsByClass("album")
            val albumList = ArrayList<Album>(count)
            for (i in 0..count - 1) {
                val coverUrl = albums[i].getElementsByClass("image").first()
                        .select("img").first().attr("src")
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
