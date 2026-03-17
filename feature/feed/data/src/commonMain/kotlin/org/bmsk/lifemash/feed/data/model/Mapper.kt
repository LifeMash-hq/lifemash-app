package org.bmsk.lifemash.feed.data.model

import org.bmsk.lifemash.data.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import kotlin.time.Instant

fun LifeMashArticleResponse.toDomain(): Article? {
    val publishedAt = this.publishedAt ?: return null
    val link = this.link?.takeIf { it.startsWith("http") } ?: return null

    return Article(
        id = ArticleId.from(this.id),
        publisher = this.publisher ?: Article.UNKNOWN_PUBLISHER,
        title = this.title.orEmpty(),
        summary = this.summary.orEmpty(),
        link = ArticleUrl.from(link),
        image = this.image?.takeIf { it.isNotBlank() },
        publishedAt = Instant.fromEpochSeconds(publishedAt),
        categories = this.categories.map { ArticleCategory.fromKey(it) },
    )
}
