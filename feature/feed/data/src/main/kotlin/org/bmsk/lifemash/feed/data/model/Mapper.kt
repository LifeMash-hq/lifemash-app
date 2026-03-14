package org.bmsk.lifemash.feed.data.model

import org.bmsk.lifemash.data.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import org.bmsk.lifemash.model.ImageUrl
import org.bmsk.lifemash.model.Publisher
import java.time.Instant

internal fun LifeMashArticleResponse.toDomain(): Article {
    val nonNullPublishedAt = requireNotNull(this.publishedAt) { "publishedAt cannot be null" }
    val nonNullLink = requireNotNull(this.link) { "link cannot be null" }

    return Article(
        id = ArticleId.from(this.id),
        publisher = Publisher.from(this.publisher ?: Publisher.unknown.name),
        title = this.title ?: "",
        summary = this.summary ?: "",
        link = ArticleUrl.from(nonNullLink),
        image = this.image?.let { ImageUrl.from(it) },
        publishedAt = Instant.ofEpochSecond(nonNullPublishedAt),
        categories = this.categories.map { ArticleCategory.fromKey(it) }
    )
}
