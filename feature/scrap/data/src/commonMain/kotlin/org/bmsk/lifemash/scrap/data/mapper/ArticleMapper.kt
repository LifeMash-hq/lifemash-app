package org.bmsk.lifemash.scrap.data.mapper

import org.bmsk.lifemash.scrap.data.entity.ArticleEntity
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import org.bmsk.lifemash.model.ImageUrl
import org.bmsk.lifemash.model.Publisher

fun ArticleEntity.toDomain() = Article(
    id = ArticleId.from(id),
    publisher = Publisher.from(publisher),
    title = title,
    summary = summary,
    link = ArticleUrl.from(link),
    image = image?.let { ImageUrl.from(it) },
    publishedAt = publishedAt,
    categories = categories
)

fun Article.toEntity() = ArticleEntity(
    id = id.value,
    publisher = publisher.name,
    title = title,
    summary = summary,
    link = link.value,
    image = image?.value,
    publishedAt = publishedAt,
    categories = categories
)
