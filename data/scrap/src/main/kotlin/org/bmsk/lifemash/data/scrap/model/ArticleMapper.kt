package org.bmsk.lifemash.data.scrap.model

import org.bmsk.lifemash.data.scrap.entity.ArticleEntity
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.ImageUrl
import org.bmsk.lifemash.domain.core.model.Publisher

internal fun ArticleEntity.toDomain() = Article(
    id = ArticleId.from(id),
    publisher = Publisher.from(publisher),
    title = title,
    summary = summary,
    link = ArticleUrl.from(link),
    image = image?.let { ImageUrl.from(it) },
    publishedAt = publishedAt,
    categories = categories
)

internal fun Article.toEntity() = ArticleEntity(
    id = id.value,
    publisher = publisher.name,
    title = title,
    summary = summary,
    link = link.value,
    image = image?.value,
    publishedAt = publishedAt,
    categories = categories
)
