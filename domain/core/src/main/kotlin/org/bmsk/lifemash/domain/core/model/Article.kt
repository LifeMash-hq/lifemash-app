package org.bmsk.lifemash.domain.core.model

import java.time.Instant

@JvmInline
value class ArticleId(val value: String)

@JvmInline
value class Publisher(val name: String)

@JvmInline
value class ArticleUrl(val value: String)

@JvmInline
value class ImageUrl(val value: String)

data class Article(
    val id: ArticleId,
    val publisher: Publisher,
    val title: String,
    val summary: String,
    val link: ArticleUrl,
    val image: ImageUrl?,
    val publishedAt: Instant,
    val categories: List<ArticleCategory>
) {
    // Example of business logic that could be added later,
    // as learned from the articles.
    fun isRecent(now: Instant): Boolean {
        // e.g., published within the last 24 hours
        return now.minusSeconds(24 * 60 * 60).isBefore(publishedAt)
    }
}
