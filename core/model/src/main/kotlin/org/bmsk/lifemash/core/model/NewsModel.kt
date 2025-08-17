package org.bmsk.lifemash.core.model

import java.util.Date

data class NewsModel(
    val title: String,
    val link: String,
    val pubDate: Date,
    val source: String? = null,
    val imageUrl: String? = null,
)
