package org.bmsk.lifemash.core.repo.scrap.impl

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.impl.entity.NewsEntity

fun NewsModel.toEntity(): NewsEntity = NewsEntity(
    title = this.title,
    link = this.link,
    pubDate = this.pubDate,
    imageUrl = this.imageUrl,
)

fun NewsEntity.toModel(): NewsModel = NewsModel(
    title = this.title,
    link = this.link,
    pubDate = this.pubDate,
    imageUrl = this.imageUrl,
)