package org.bmsk.lifemash.core.repo.article.api

data class Article(
    val id: String,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: String,
    val image: String?,
    val publishedAt: Long,
    val host: String,
    val categories: List<ArticleCategory>
)
