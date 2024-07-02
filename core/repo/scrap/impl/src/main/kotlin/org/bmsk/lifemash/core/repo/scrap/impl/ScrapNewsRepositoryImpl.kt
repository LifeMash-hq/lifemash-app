package org.bmsk.lifemash.core.repo.scrap.impl

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import org.bmsk.lifemash.core.repo.scrap.impl.dao.ScrapNewsDao
import java.util.Date
import javax.inject.Inject

internal class ScrapNewsRepositoryImpl @Inject constructor(
    private val scrapNewsDao: ScrapNewsDao,
) : ScrapNewsRepository {

    override fun addNewsToDB(newsModel: NewsModel) {
        TODO("Not yet implemented")
    }

    override fun getNewsFromDB(): List<NewsModel> {
        TODO("Not yet implemented")
    }

    override fun deleteNewsFromDB(newsModel: NewsModel) {
        TODO("Not yet implemented")
    }

    override fun deleteAllNewsFromDB() {
        TODO("Not yet implemented")
    }

    override fun updateNewsFromDB(newsModel: NewsModel) {
        scrapNewsDao.updateNews(newsModel.toEntity())
    }

    override fun updateAllNewsFromDB(newsModels: List<NewsModel>) {
        TODO("Not yet implemented")
    }

    override fun getNewsByTitle(title: String): NewsModel? {
        TODO("Not yet implemented")
    }

    override fun getNewsByLink(link: String): NewsModel? {
        TODO("Not yet implemented")
    }

    override fun getNewsByPubDate(pubDate: Date): NewsModel? {
        TODO("Not yet implemented")
    }

    override fun getNewsByImageUrl(imageUrl: String): NewsModel? {
        TODO("Not yet implemented")
    }

    override fun getNewsByContent(content: String): NewsModel? {
        TODO("Not yet implemented")
    }
}
