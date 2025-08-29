package org.bmsk.lifemash.data.search.transform

import org.bmsk.lifemash.core.model.DateParser
import org.bmsk.lifemash.core.network.response.NewsItem
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.Publisher
import java.time.Instant

internal fun NewsItem.toDomain(): Article {
    // NewsItem doesn't have publisher, summary, categories, host.
    // pubDate is Date, not Long.
    val publishedInstant =
        this.pubDate?.let { DateParser.parseDate(it).toInstant() } ?: Instant.EPOCH
    return Article(
        id = ArticleId(this.link ?: ""),
        publisher = Publisher("Unknown"),
        title = this.title ?: "",
        summary = "",
        link = ArticleUrl(this.link ?: ""),
        image = null,
        publishedAt = publishedInstant,
        categories = emptyList()
    )
}