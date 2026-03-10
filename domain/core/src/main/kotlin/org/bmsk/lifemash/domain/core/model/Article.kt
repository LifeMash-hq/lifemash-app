package org.bmsk.lifemash.domain.core.model

import java.time.Instant

@JvmInline
value class ArticleId private constructor(val value: String) {
    companion object {
        fun from(raw: String): ArticleId {
            require(raw.isNotBlank()) { "ArticleId must not be blank" }
            return ArticleId(raw)
        }
    }
}

@JvmInline
value class Publisher private constructor(val name: String) {
    companion object {
        val unknown: Publisher = Publisher("Unknown")

        fun from(raw: String): Publisher {
            require(raw.isNotBlank()) { "Publisher must not be blank" }
            return Publisher(raw)
        }
    }
}

@JvmInline
value class ArticleUrl private constructor(val value: String) {
    companion object {
        fun from(raw: String): ArticleUrl {
            require(raw.startsWith("http")) { "Invalid ArticleUrl: $raw" }
            return ArticleUrl(raw)
        }
    }
}

@JvmInline
value class ImageUrl private constructor(val value: String) {
    companion object {
        fun from(raw: String): ImageUrl {
            require(raw.isNotBlank()) { "ImageUrl must not be blank" }
            return ImageUrl(raw)
        }
    }
}

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
