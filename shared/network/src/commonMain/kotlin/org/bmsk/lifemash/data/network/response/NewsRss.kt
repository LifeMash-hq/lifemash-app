package org.bmsk.lifemash.data.network.response

data class NewsRss(
    val channel: RssChannel,
)

data class RssChannel(
    val title: String,
    val items: List<NewsItem>? = null,
)

data class NewsItem(
    val title: String? = null,
    val link: String? = null,
    val pubDate: String? = null,
)
