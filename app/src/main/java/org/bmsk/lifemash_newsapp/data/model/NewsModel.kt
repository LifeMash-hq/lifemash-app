package org.bmsk.lifemash_newsapp.data.model

data class NewsModel(
    val title: String,
    val link: String,
    var imageUrl: String? = null,
)
