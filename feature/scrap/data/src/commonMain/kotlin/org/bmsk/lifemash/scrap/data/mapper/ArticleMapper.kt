package org.bmsk.lifemash.scrap.data.mapper

import org.bmsk.lifemash.scrap.data.entity.ArticleEntity
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl

fun ArticleEntity.toDomain() = Article(
    id = ArticleId.from(id),
    publisher = publisher,
    title = title,
    summary = summary,
    link = ArticleUrl.from(link),
    image = image,
    publishedAt = publishedAt,
    categories = categories
)

fun Article.toEntity() = ArticleEntity(
    id = id.toString(),
    publisher = publisher,
    title = title,
    summary = summary,
    link = link.toString(),
    image = image,
    publishedAt = publishedAt,
    categories = categories
)
