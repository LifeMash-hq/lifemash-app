package org.bmsk.lifemash.core.network.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "rss")
data class NewsRss(
    @param:Element(name = "channel")
    val channel: RssChannel,
)

@Xml(name = "channel")
data class RssChannel(
    @param:PropertyElement(name = "title")
    val title: String,
    @param:Element(name = "item")
    val items: List<NewsItem>? = null,
)

@Xml(name = "item")
data class NewsItem(
    @param:PropertyElement(name = "title")
    val title: String? = null,
    @param:PropertyElement(name = "link")
    val link: String? = null,
    @param:PropertyElement(name = "pubDate")
    val pubDate: String? = null,
)
