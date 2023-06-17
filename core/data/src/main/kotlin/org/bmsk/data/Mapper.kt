package org.bmsk.data

import org.bmsk.model.NewsModel
import org.bmsk.network.model.NewsItem

fun List<NewsItem>.asDomain(): List<NewsModel> {
    return this.map {
        NewsModel(
            title = it.title ?: "",
            link = it.link ?: "",
            imageUrl = null
        )
    }
}