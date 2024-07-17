package org.bmsk.lifemash.core.repo.search.impl

import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.network.model.NewsItem
import java.util.Date

internal fun List<NewsItem>.asModel(): List<NewsModel> {
    return this.map {
        NewsModel(
            title = it.title ?: "",
            link = it.link ?: "",
            pubDate = it.pubDate?.let(DateParser::parseDate) ?: Date(),
            imageUrl = null,
        )
    }
}
