package org.bmsk.lifemash.feed.data.history.entity

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import kotlin.time.Instant

fun ReadingRecordEntity.toDomain(): Article = Article(
    id = ArticleId.from(articleId),
    publisher = publisher,
    title = title,
    summary = summary,
    link = ArticleUrl.from(link),
    image = image,
    publishedAt = publishedAt,
    categories = categories,
)

fun Article.toReadingRecordEntity(readAt: Instant): ReadingRecordEntity =
    ReadingRecordEntity(
        articleId = id.toString(),
        readAt = readAt,
        publisher = publisher,
        title = title,
        summary = summary,
        link = link.toString(),
        image = image,
        publishedAt = publishedAt,
        categories = categories,
    )
