package org.bmsk.lifemash.feed.data.history.entity

import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.model.ArticleUrl
import org.bmsk.lifemash.model.ImageUrl
import org.bmsk.lifemash.model.Publisher
import kotlinx.datetime.Instant

fun ReadingRecordEntity.toDomain(): Article = Article(
    id = ArticleId.from(articleId),
    publisher = Publisher.from(publisher),
    title = title,
    summary = summary,
    link = ArticleUrl.from(link),
    image = image?.let { ImageUrl.from(it) },
    publishedAt = publishedAt,
    categories = categories,
)

fun Article.toReadingRecordEntity(readAt: Instant): ReadingRecordEntity =
    ReadingRecordEntity(
        articleId = id.value,
        readAt = readAt,
        publisher = publisher.name,
        title = title,
        summary = summary,
        link = link.value,
        image = image?.value,
        publishedAt = publishedAt,
        categories = categories,
    )
