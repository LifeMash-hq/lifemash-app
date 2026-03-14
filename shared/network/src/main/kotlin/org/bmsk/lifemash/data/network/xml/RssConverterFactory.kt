package org.bmsk.lifemash.data.network.xml

import okhttp3.ResponseBody
import org.bmsk.lifemash.data.network.response.NewsRss
import org.bmsk.lifemash.data.network.response.NewsItem
import org.bmsk.lifemash.data.network.response.RssChannel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class RssConverterFactory private constructor() : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        if (type != NewsRss::class.java) return null
        return Converter<ResponseBody, NewsRss> { body ->
            body.use { parseRss(it.string()) }
        }
    }

    private fun parseRss(xml: String): NewsRss {
        val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        val parser = factory.newPullParser().apply { setInput(xml.reader()) }

        val items = mutableListOf<NewsItem>()
        var channelTitle = ""
        var itemTitle: String? = null
        var itemLink: String? = null
        var itemPubDate: String? = null
        var currentTag = ""
        var inItem = false
        var inChannel = false

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    when (currentTag) {
                        "channel" -> inChannel = true
                        "item" -> {
                            inItem = true
                            itemTitle = null
                            itemLink = null
                            itemPubDate = null
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isEmpty()) {
                        eventType = parser.next()
                        continue
                    }
                    if (inItem) {
                        when (currentTag) {
                            "title" -> itemTitle = text
                            "link" -> itemLink = text
                            "pubDate" -> itemPubDate = text
                        }
                    } else if (inChannel && currentTag == "title") {
                        channelTitle = text
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "item" && inItem) {
                        items += NewsItem(
                            title = itemTitle,
                            link = itemLink,
                            pubDate = itemPubDate,
                        )
                        inItem = false
                    }
                    currentTag = ""
                }
            }
            eventType = parser.next()
        }

        return NewsRss(channel = RssChannel(title = channelTitle, items = items))
    }

    companion object {
        fun create(): RssConverterFactory = RssConverterFactory()
    }
}
