package org.bmsk.lifemash.data.scrap.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.data.scrap.dao.ScrapArticleDao
import org.bmsk.lifemash.data.scrap.model.toDomain
import org.bmsk.lifemash.data.scrap.model.toEntity
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository
import javax.inject.Inject

internal class ScrapRepositoryImpl @Inject constructor(
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
            scrapArticleDao.deleteArticle(articleId.value)
        }
    }
}