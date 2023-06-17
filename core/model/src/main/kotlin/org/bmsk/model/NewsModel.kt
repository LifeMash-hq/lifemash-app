package org.bmsk.model

data class NewsModel(
    val title: String,
    val link: String,
    var imageUrl: String? = null,
)
