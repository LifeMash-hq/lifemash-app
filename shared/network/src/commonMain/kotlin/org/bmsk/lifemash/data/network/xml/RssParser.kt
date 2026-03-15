package org.bmsk.lifemash.data.network.xml

import com.fleeksoft.ksoup.Ksoup
import org.bmsk.lifemash.data.network.response.NewsItem
import org.bmsk.lifemash.data.network.response.NewsRss
import org.bmsk.lifemash.data.network.response.RssChannel

internal object RssParser {
    fun parse(xml: String): NewsRss {
        val doc = Ksoup.parse(xml)
        val channelTitle = doc.selectFirst("channel > title")?.text().orEmpty()
        val items = doc.select("item").map { item ->
            NewsItem(
                title = item.selectFirst("title")?.text(),
                link = item.selectFirst("link")?.text()
                    ?: item.selectFirst("link")?.nextSibling()?.toString()?.trim(),
                pubDate = item.selectFirst("pubDate")?.text(),
            )
        }
        return NewsRss(channel = RssChannel(title = channelTitle, items = items))
    }
}
