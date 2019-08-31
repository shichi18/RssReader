package com.github.shichi18.rssreader

import android.content.AsyncTaskLoader
import android.content.Context
import org.w3c.dom.NodeList
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

//Rssの各記事を表すデータクラス
data class Article(val title: String, val link: String, val updated: Date)

//RSSを表現するデータクラス
data class Rss(val title: String, val updated: Date, val articles: List<Article>)

//RSSをパース
fun parseRss(stream: InputStream): Rss {

    // XML->DOMオブジェクトに変換
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream)
    stream.close()
    //Xpathを生成
    val xPath = XPathFactory.newInstance().newXPath()
    //日付(ex:2019-08-27T10:04:00-04:00)
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH)
    //feed内の<entry>
    val entries = xPath.evaluate("/feed/entry", doc, XPathConstants.NODESET) as NodeList
    //RSS記事一覧
    val articles = arrayListOf<Article>()

    for (i in 0 until entries.length) {
        val item = entries.item(i)

        val article = Article(
            title = xPath.evaluate("./title/text()", item),
            link = xPath.evaluate("./link/@href", item),
            updated = formatter.parse(xPath.evaluate("./updated/text()", item))
        )
        articles.add(article)

    }
    //RSSオブジェクトをまとめて返す
    return Rss(
        title = xPath.evaluate("/feed/entry/title/text()", doc),
        updated = formatter.parse(xPath.evaluate("/feed/entry/updated/text()", doc)),
        articles = articles
    )
}

//ローダ用
class RssLoader(context: Context) : AsyncTaskLoader<Rss>(context) {

    private var cache: Rss? = null

    override fun loadInBackground(): Rss? {
        val response = httpGet("https://martinfowler.com/feed.atom")
        if (response != null) {
            return parseRss(response)
        }
        return null
    }

    override fun deliverResult(data: Rss?) {
        if (isReset || data == null) return
        cache = data
        super.deliverResult(data)
    }

    override fun onStartLoading() {
        if (cache != null) {
            deliverResult(cache)
        }
        if (takeContentChanged() || cache == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}
