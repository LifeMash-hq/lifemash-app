package org.bmsk.lifemash.data.scrap.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun getScrappedArticles(): Flow<List<Article>> {
        return scrapNewsDao.getAllNews().map { entities ->
            entities.map { it.toDomain() }
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
