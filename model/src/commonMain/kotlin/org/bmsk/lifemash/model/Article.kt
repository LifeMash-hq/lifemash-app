package org.bmsk.lifemash.model

import kotlin.time.Instant
import kotlin.jvm.JvmInline

@JvmInline
value class ArticleId private constructor(private val raw: String) {
    override fun toString(): String = raw

    companion object {
        fun from(raw: String): ArticleId {
            require(raw.isNotBlank()) { "ArticleId must not be blank" }
            return ArticleId(raw)
        }
    }
}

@JvmInline
value class ArticleUrl private constructor(private val raw: String) {
    val host: String get() = raw.substringAfter("://").substringBefore("/")

    override fun toString(): String = raw

    companion object {
        fun from(raw: String): ArticleUrl {
            require(raw.startsWith("http")) { "Invalid ArticleUrl: $raw" }
            return ArticleUrl(raw)
        }
    }
}

data class Article(
    val id: ArticleId,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: ArticleUrl,
    val image: String?,
    val publishedAt: Instant,
    val categories: List<ArticleCategory>,
) {
    fun isRecent(now: Instant): Boolean {
        return (now - publishedAt).inWholeSeconds < 86400
    }

    companion object {
        const val UNKNOWN_PUBLISHER = "Unknown"
    }
}
