package com.example.wenhai.listenall.utils

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

    @JvmStatic
    var client: OkHttpClient? = null

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
    fun getForXiami(url: String, callback: ResponseCallBack) {
        //start request
        LogUtil.d(TAG, url)
        callback.onStart()
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
                    callback.onFailure(e.localizedMessage)
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response == null) {
                    callback.onFailure("response == null")
                } else {
                    val body = response.body() !!.string()
                    LogUtil.d("response:", body)
                    if (body.startsWith("jsonp")) {
                        val realJsonStr = body.substring(body.indexOf("(") + 1, body.lastIndexOf(")"))
                        callback.onJsonObjectResponse(JSONObject(realJsonStr).getJSONObject("data"))
                    } else if (body.startsWith("{")) {
                        callback.onJsonObjectResponse(JSONObject(body))
                    } else if (body.contains("</")) {
                        callback.onHtmlResponse(body)
                    } else {
                        callback.onFailure("response body:$body")
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