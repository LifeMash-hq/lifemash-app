package org.bmsk.core.model

data class NewsModel(
    val title: String,
    val link: String,
    val pubDate: String,
    val imageUrl: String? = null,
)
