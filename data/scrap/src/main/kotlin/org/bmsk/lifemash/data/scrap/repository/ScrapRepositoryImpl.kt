package org.bmsk.lifemash.data.scrap.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.data.scrap.dao.ScrapNewsDao
import org.bmsk.lifemash.data.scrap.model.fromDomain
import org.bmsk.lifemash.data.scrap.model.toDomain
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

internal class ScrapRepositoryImpl @Inject constructor(
    private val scrapNewsDao: ScrapNewsDao
) : ScrapRepository {

    override suspend fun getScrappedArticles(): List<Article> {
        return withContext(Dispatchers.IO) {
            scrapNewsDao.getAllNews().map { it.toDomain() }
        }
    }

    override suspend fun addScrappedArticle(article: Article) {
        withContext(Dispatchers.IO) {
            scrapNewsDao.insertNews(article.fromDomain())
        }
    }

    override suspend fun deleteScrappedArticle(articleId: ArticleId) {
        withContext(Dispatchers.IO) {
            scrapNewsDao.deleteNewsByLink(articleId.value)
        }
    }
}
