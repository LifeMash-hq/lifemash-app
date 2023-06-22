package org.bmsk.interactor

import org.bmsk.database.model.entity.NewsEntity
import org.bmsk.model.NewsModel
import java.util.Date

fun NewsModel.asEntity(createdDate: Date) = NewsEntity(
    title = title,
    link = link,
    pubDate = pubDate,
    imageUrl = imageUrl,
    createdDate = createdDate
)

fun NewsEntity.asModel() = NewsModel(
    title = title,
    link = link,
    pubDate = pubDate,
    imageUrl = imageUrl
)