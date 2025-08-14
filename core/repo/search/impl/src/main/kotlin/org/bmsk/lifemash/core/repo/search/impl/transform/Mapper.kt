package org.bmsk.lifemash.core.repo.search.impl.transform

import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.network.response.NewsItem
import org.bmsk.lifemash.core.network.response.LifeMashArticle
import java.util.Date

internal fun NewsItem.toModel(): NewsModel {
    return NewsModel(
        title = this.title ?: "",
        link = this.link ?: "",
        pubDate = this.pubDate?.let(DateParser::parseDate) ?: Date(),
        imageUrl = null,
    )
}

internal fun LifeMashArticle.toModel(): NewsModel {
    return NewsModel(
        title = this.title,
        link = this.url,
        pubDate = this.published.let(DateParser::parseDate),
        imageUrl = this.imageUrl
    )
}