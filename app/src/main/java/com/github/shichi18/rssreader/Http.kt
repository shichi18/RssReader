package com.github.shichi18.rssreader

import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun httpGet(url: String): InputStream? {
    //通信接続用のオブジェクト生成
    val con = URL(url).openConnection() as HttpURLConnection

    //接続の設定
    con.apply {
        requestMethod = "GET"
        connectTimeout = 3000
        readTimeout = 5000
        instanceFollowRedirects = true
    }

    //接続
    con.connect()

    //ステータスコード
    if (con.responseCode in 200..299) {
        //成功したらレスポンスの入力ストリームをBufferedInputStreamとして返す
        return BufferedInputStream(con.inputStream)
    }
    //失敗
    return null
}
