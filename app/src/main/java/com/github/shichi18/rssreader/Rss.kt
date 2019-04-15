package com.github.shichi18.rssreader

import org.w3c.dom.NodeList
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

//Rssの各記事を表すデータクラス
data class Article(val title: String, val link: String, val pubDate: Date)

//RSSを表現するデータクラス
data class Rss(val title: String, val pubDate: Date, val articles: List<Article>)

//RSSをパースする

fun parseRss(stream: InputStream): Rss {
    //xmlをDomに変換
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(stream)
    stream.close()

    //Xpathを生成
    val xPath = XPathFactory.newInstance().newXPath()

    //日付
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)

    //チャンネル内の<item>
    val items = xPath.evaluate("/rss/channel/item", doc, XPathConstants.NODESET) as NodeList

    //RSS記事一覧
    val articles = arrayListOf<Article>()

    for (i in 0 until items.length) {
        val item = items.item(i)

        val article = Article(
            title = xPath.evaluate("./title/text()", item),
            link = xPath.evaluate("./link/text()", item),
            pubDate = formatter.parse(xPath.evaluate("./pubDate/text()", item))
        )
        articles.add(article)
        
    }

    //RSSオブジェクトをまとめて返す
    return Rss(
        title = xPath.evaluate("/rss/channel/title/text()", doc),
        pubDate = formatter.parse(xPath.evaluate("/rss/channel/pubDate/text()", doc)),
        articles = articles
    )
}
