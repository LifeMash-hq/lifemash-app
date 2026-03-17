package org.bmsk.lifemash.scrap.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.scrap.data.dao.ScrapArticleDao
import org.bmsk.lifemash.scrap.data.mapper.toDomain
import org.bmsk.lifemash.scrap.data.mapper.toEntity
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleId
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository

class ScrapRepositoryImpl(
    private val scrapArticleDao: ScrapArticleDao
) : ScrapRepository {

    override fun getScrappedArticles(): Flow<List<Article>> {
        return scrapArticleDao.getAllArticles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addScrappedArticle(article: Article) {
        withContext(Dispatchers.IO) {
            scrapArticleDao.insertArticle(article.toEntity())
        }
    }

    override suspend fun deleteScrappedArticle(articleId: ArticleId) {
        withContext(Dispatchers.IO) {
            scrapArticleDao.deleteArticle(articleId.toString())
        }
    }
}
