package com.example.wenhai.listenall.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import com.example.wenhai.listenall.common.Config
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object OkHttpUtil {
    const val TAG = "OkHttpUtil"
    private const val NETWORK_NOT_AVAILABLE = 0
    const val NETWORK_AVAILABLE = 1
    const val ARG_NETWORK_STATE = "state"
    const val ARG_NETWORK_DESC = "msg"

    @JvmStatic
    private var client: OkHttpClient? = null

    @JvmStatic
    fun getHttpClient(): OkHttpClient {
        if (client == null) {
            synchronized(OkHttpUtil::class.java) {
                if (client == null) {
                    client = OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build()
                }
            }
        }
        return client as OkHttpClient
    }

    @JvmStatic
    fun checkNetWork(context: Context): Bundle {
        val conn: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = conn.activeNetworkInfo
        val connect = if (activeNetwork != null && activeNetwork.isConnected) {
            activeNetwork.type
        } else {
            -1
        }
        val bundle = Bundle()
        if (connect != -1) {//有网络连接
            val sp = context.getSharedPreferences(Config.NAME, Context.MODE_PRIVATE)
            val onlyWifi = sp.getBoolean(Config.ONLY_WIFI, false)
            //判断用户设置
            if (onlyWifi && connect == ConnectivityManager.TYPE_WIFI || !onlyWifi) {
                bundle.putInt(ARG_NETWORK_STATE, NETWORK_AVAILABLE)
            } else {
                bundle.putInt(ARG_NETWORK_STATE, NETWORK_NOT_AVAILABLE)
                bundle.putString(ARG_NETWORK_DESC, "非wifi网络自动停止加载")
            }
        } else {//没有网络连接
            bundle.putInt(ARG_NETWORK_STATE, NETWORK_NOT_AVAILABLE)
            bundle.putString(ARG_NETWORK_DESC, "没有网络")
        }
        return bundle
    }

    @JvmStatic
    fun getForXiami(context: Context, url: String, callback: ResponseCallBack) {
        //start request
        LogUtil.d(TAG, url)
        callback.onStart()
        val networkData = checkNetWork(context)
        if (networkData.getInt("state", 0) == 0) {
            callback.onFailure(networkData.getString("msg"))
            LogUtil.e("test", networkData.getString("msg"))
            return
        }
        val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Referer", "http://m.xiami.com/")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
                .get()
                .build()
        val newCall = getHttpClient().newCall(request)

        newCall.enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                if (e != null) {
                    callback.onFailure("请求失败")
                    getHttpClient().dispatcher().cancelAll()
                }

            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response == null) {
                    callback.onFailure("response == null")
                } else {
                    val body = response.body()!!.string()
                    when {
                        body.startsWith("jsonp") -> {
                            val realJsonStr = body.substring(body.indexOf("(") + 1, body.lastIndexOf(")"))
                            callback.onJsonObjectResponse(JSONObject(realJsonStr).getJSONObject("data"))
                        }
                        body.startsWith("{") -> callback.onJsonObjectResponse(JSONObject(body))
                        body.contains("</") -> callback.onHtmlResponse(body)
                        else -> {
                            Log.d(TAG, "$body ")
                            callback.onFailure("response body:$body")
                        }
                    }
                }

            }

        })
    }

}

open class BaseResponseCallback : ResponseCallBack {

    override fun onStart() {
    }

    override fun onResponse(response: Response) {
    }

    override fun onJsonObjectResponse(jsonObject: JSONObject) {
    }

    override fun onJsonArrayResponse(jsonArray: JSONArray) {

    }

    override fun onHtmlResponse(html: String) {
    }

    override fun onFailure(msg: String) {
    }

}


interface ResponseCallBack {
    fun onStart()
    fun onResponse(response: Response)
    fun onJsonObjectResponse(jsonObject: JSONObject)
    fun onJsonArrayResponse(jsonArray: JSONArray)
    fun onHtmlResponse(html: String)
    fun onFailure(msg: String)
}