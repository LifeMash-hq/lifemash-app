package org.bmsk.lifemash.core.repo.article

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.network.response.LifeMashArticleCategory
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.core.repo.article.api.Article
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory
import org.bmsk.lifemash.core.repo.article.api.ArticleRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ArticleRepositoryImpl @Inject constructor(
    private val lifeMashFirebaseService: LifeMashFirebaseService,
) : ArticleRepository {

    // 기본 TTL(Time To Live): 5분
    private val defaultTtlMillis: Long = 5 * 60 * 1000L

    private data class Cached(
        val data: List<Article>,
        val fetchedAt: Long
    )

    private val cache = ConcurrentHashMap<ArticleCategory, Cached>()
    private val mutexMap = ConcurrentHashMap<ArticleCategory, Mutex>()

    override suspend fun getArticles(
        category: ArticleCategory
    ): List<Article> = getArticles(category, ttlMillis = defaultTtlMillis, forceRefresh = false)

    // 필요 시 외부에서 TTL/강제 갱신 제어
    suspend fun getArticles(
        category: ArticleCategory,
        ttlMillis: Long = defaultTtlMillis,
        forceRefresh: Boolean = false
    ): List<Article> {
        val mutex = mutexMap.getOrPut(category) { Mutex() }

        return mutex.withLock {
            val now = System.currentTimeMillis()
            val cached = cache[category]
            val isFresh = cached != null && (now - cached.fetchedAt) <= ttlMillis

            if (!forceRefresh && isFresh) {
                return@withLock cached.data
            }

            val fresh = fetchFromNetwork(category)
            cache[category] = Cached(fresh, now)

            return@withLock fresh
        }
    }

    private suspend fun fetchFromNetwork(category: ArticleCategory): List<Article> {
        return withContext(Dispatchers.IO) {
            lifeMashFirebaseService
                .getArticles(category = category.toServiceCategory())
                .mapNotNull { article ->
                    runCatching {
                        Article(
                            id = article.id,
                            publisher = requireNotNull(article.publisher),
                            title = requireNotNull(article.title),
                            summary = article.summary ?: "",
                            link = requireNotNull(article.link),
                            image = article.image,
                            publishedAt = requireNotNull(article.publishedAt),
                            host = requireNotNull(article.host),
                            categories = article.categories.map { it.toArticleCategory() }
                        )
                    }.getOrNull()
                }
        }
    }

    private fun ArticleCategory.toServiceCategory(): LifeMashArticleCategory = when (this) {
        ArticleCategory.ALL -> LifeMashArticleCategory.ALL
        ArticleCategory.POLITICS -> LifeMashArticleCategory.POLITICS
        ArticleCategory.ECONOMY -> LifeMashArticleCategory.ECONOMY
        ArticleCategory.SOCIETY -> LifeMashArticleCategory.SOCIETY
        ArticleCategory.INTERNATIONAL -> LifeMashArticleCategory.INTERNATIONAL
        ArticleCategory.SPORTS -> LifeMashArticleCategory.SPORTS
        ArticleCategory.CULTURE -> LifeMashArticleCategory.CULTURE
        ArticleCategory.ENTERTAINMENT -> LifeMashArticleCategory.ENTERTAINMENT
        ArticleCategory.TECH -> LifeMashArticleCategory.TECH
        ArticleCategory.SCIENCE -> LifeMashArticleCategory.SCIENCE
        ArticleCategory.COLUMN -> LifeMashArticleCategory.COLUMN
        ArticleCategory.PEOPLE -> LifeMashArticleCategory.PEOPLE
        ArticleCategory.HEALTH -> LifeMashArticleCategory.HEALTH
        ArticleCategory.MEDICAL -> LifeMashArticleCategory.MEDICAL
        ArticleCategory.WOMEN -> LifeMashArticleCategory.WOMEN
        ArticleCategory.CARTOON -> LifeMashArticleCategory.CARTOON
    }

    private fun LifeMashArticleCategory.toArticleCategory(): ArticleCategory = when (this) {
        LifeMashArticleCategory.ALL -> ArticleCategory.ALL
        LifeMashArticleCategory.POLITICS -> ArticleCategory.POLITICS
        LifeMashArticleCategory.ECONOMY -> ArticleCategory.ECONOMY
        LifeMashArticleCategory.SOCIETY -> ArticleCategory.SOCIETY
        LifeMashArticleCategory.INTERNATIONAL -> ArticleCategory.INTERNATIONAL
        LifeMashArticleCategory.SPORTS -> ArticleCategory.SPORTS
        LifeMashArticleCategory.CULTURE -> ArticleCategory.CULTURE
        LifeMashArticleCategory.ENTERTAINMENT -> ArticleCategory.ENTERTAINMENT
        LifeMashArticleCategory.TECH -> ArticleCategory.TECH
        LifeMashArticleCategory.SCIENCE -> ArticleCategory.SCIENCE
        LifeMashArticleCategory.COLUMN -> ArticleCategory.COLUMN
        LifeMashArticleCategory.PEOPLE -> ArticleCategory.PEOPLE
        LifeMashArticleCategory.HEALTH -> ArticleCategory.HEALTH
        LifeMashArticleCategory.MEDICAL -> ArticleCategory.MEDICAL
        LifeMashArticleCategory.WOMEN -> ArticleCategory.WOMEN
        LifeMashArticleCategory.CARTOON -> ArticleCategory.CARTOON
    }
}