package org.bmsk.lifemash.core.repo.scrap.api

import org.bmsk.lifemash.core.model.NewsModel
import java.util.Date

interface ScrapNewsRepository {
    fun addNewsToDB(newsModel: NewsModel)

    fun getNewsFromDB(): List<NewsModel>

    fun deleteNewsFromDB(newsModel: NewsModel)

    fun deleteAllNewsFromDB()

    fun updateNewsFromDB(newsModel: NewsModel)

    fun updateAllNewsFromDB(newsModels: List<NewsModel>)

    fun getNewsByTitle(title: String): NewsModel?

    fun getNewsByLink(link: String): NewsModel?

    fun getNewsByPubDate(pubDate: Date): NewsModel?

    fun getNewsByImageUrl(imageUrl: String): NewsModel?

    fun getNewsByContent(content: String): NewsModel?
}
