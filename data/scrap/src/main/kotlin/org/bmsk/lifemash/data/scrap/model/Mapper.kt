package org.bmsk.lifemash.data.scrap.model

import org.bmsk.lifemash.data.scrap.entity.NewsEntity
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.core.model.ArticleUrl
import org.bmsk.lifemash.domain.core.model.ImageUrl
import org.bmsk.lifemash.domain.core.model.Publisher
import java.util.Date

fun NewsEntity.toDomain(): Article {
    // This is a simplification. The original NewsEntity doesn't have all the fields
    // for a full Article object (like publisher, summary, categories).
    // I will have to make some assumptions or leave them as default values.
    return Article(
        id = ArticleId(this.link), // Using link as ID
        publisher = Publisher("Scrapped"), // No publisher info in NewsEntity
        title = this.title,
        summary = "", // No summary info in NewsEntity
        link = ArticleUrl(this.link),
        image = this.imageUrl?.let { ImageUrl(it) },
        publishedAt = this.pubDate.toInstant(),
        categories = emptyList() // No category info in NewsEntity
    )
}

fun Article.fromDomain(): NewsEntity {
    return NewsEntity(
        title = this.title,
        link = this.link.value,
        pubDate = Date.from(this.publishedAt),
        imageUrl = this.image?.value
    )
}
