package org.bmsk.lifemash.core.repo.scrap.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.repo.scrap.api.ScrapNewsRepository
import org.bmsk.lifemash.core.repo.scrap.impl.dao.ScrapNewsDao
import org.bmsk.lifemash.core.repo.scrap.impl.entity.NewsEntity
import java.util.Date
import javax.inject.Inject

internal class ScrapNewsRepositoryImpl @Inject constructor(
    private val scrapNewsDao: ScrapNewsDao,
) : ScrapNewsRepository {

    override suspend fun addNewsToDB(newsModel: NewsModel) {
        Dispatchers.IO { scrapNewsDao.insertNews(newsModel.toEntity()) }
    }

    override fun getNewsFromDB(): List<NewsModel> =
        scrapNewsDao.getAllNews().map(NewsEntity::toModel)

    override fun deleteNewsFromDB(newsModel: NewsModel) {
        scrapNewsDao.deleteNews(newsModel.toEntity())
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

    override suspend fun getNewsByLink(link: String): NewsModel? =
        Dispatchers.IO { scrapNewsDao.getNewsByLink(link)?.toModel() }

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
