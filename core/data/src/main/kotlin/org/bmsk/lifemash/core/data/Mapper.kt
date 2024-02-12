package org.bmsk.lifemash.core.data

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.network.model.NewsItem

internal fun List<NewsItem>.asDomain(): List<NewsModel> {
    return this.map {
        NewsModel(
            title = it.title ?: "",
            link = it.link ?: "",
            pubDate = it.pubDate?.let { date -> DateParser.parseDate(date) } ?: "",
            imageUrl = null,
        )
    }
}
