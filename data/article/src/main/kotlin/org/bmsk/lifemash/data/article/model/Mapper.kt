package org.bmsk.lifemash.data.article.model

import org.bmsk.lifemash.core.network.response.LifeMashArticleCategory
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.ImageUrl
import org.bmsk.lifemash.domain.core.model.Publisher
import java.time.Instant

internal fun LifeMashArticleResponse.toDomain(): Article {
    val nonNullPublishedAt = requireNotNull(this.publishedAt) { "publishedAt cannot be null" }

    return Article(
        id = ArticleId(this.id),
        publisher = Publisher(this.publisher ?: ""),
        title = this.title ?: "",
        summary = this.summary ?: "",
        link = ArticleUrl(this.link ?: ""),
        image = this.image?.let { ImageUrl(it) },
        publishedAt = Instant.ofEpochSecond(nonNullPublishedAt),
        categories = this.categories.map { it.toDomain() }
    )
}

internal fun LifeMashArticleCategory.toDomain(): ArticleCategory = when (this) {
    LifeMashArticleCategory.ALL -> ArticleCategory.ALL
    LifeMashArticleCategory.POLITICS -> ArticleCategory.POLITICS
    LifeMashArticleCategory.ECONOMY -> ArticleCategory.ECONOMY
    LifeMashArticleCategory.SOCIETY -> ArticleCategory.SOCIETY
    LifeMashArticleCategory.INTERNATIONAL -> ArticleCategory.INTERNATIONAL
    LifeMashArticleCategory.SPORTS -> ArticleCategory.SPORTS
    LifeMashArticleCategory.CULTURE -> ArticleCategory.CULTURE
    LifeMashArticleCategory.ENTERTAINMENT -> ArticleCategory.ENTERTAINMENT
    LifeMashArticleCategory.TECH -> ArticleCategory.TECH
    LifeMashArticleCategory.SCIENCE -> ArticleCategory.SCIENCE
    LifeMashArticleCategory.COLUMN -> ArticleCategory.COLUMN
    LifeMashArticleCategory.PEOPLE -> ArticleCategory.PEOPLE
    LifeMashArticleCategory.HEALTH -> ArticleCategory.HEALTH
    LifeMashArticleCategory.MEDICAL -> ArticleCategory.MEDICAL
    LifeMashArticleCategory.WOMEN -> ArticleCategory.WOMEN
    LifeMashArticleCategory.CARTOON -> ArticleCategory.CARTOON
}

internal fun ArticleCategory.toServiceCategory(): LifeMashArticleCategory = when (this) {
    ArticleCategory.ALL -> LifeMashArticleCategory.ALL
    ArticleCategory.POLITICS -> LifeMashArticleCategory.POLITICS
    ArticleCategory.ECONOMY -> LifeMashArticleCategory.ECONOMY
    ArticleCategory.SOCIETY -> LifeMashArticleCategory.SOCIETY
    ArticleCategory.INTERNATIONAL -> LifeMashArticleCategory.INTERNATIONAL
    ArticleCategory.SPORTS -> LifeMashArticleCategory.SPORTS
    ArticleCategory.CULTURE -> LifeMashArticleCategory.CULTURE
    ArticleCategory.ENTERTAINMENT -> LifeMashArticleCategory.ENTERTAINMENT
    ArticleCategory.TECH -> LifeMashArticleCategory.TECH
    ArticleCategory.SCIENCE -> LifeMashArticleCategory.SCIENCE
    ArticleCategory.COLUMN -> LifeMashArticleCategory.COLUMN
    ArticleCategory.PEOPLE -> LifeMashArticleCategory.PEOPLE
    ArticleCategory.HEALTH -> LifeMashArticleCategory.HEALTH
    ArticleCategory.MEDICAL -> LifeMashArticleCategory.MEDICAL
    ArticleCategory.WOMEN -> LifeMashArticleCategory.WOMEN
    ArticleCategory.CARTOON -> LifeMashArticleCategory.CARTOON
}
