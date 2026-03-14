package org.bmsk.lifemash.data.article.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.data.article.model.toDomain
import org.bmsk.lifemash.domain.core.model.Article
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.feed.repository.ArticleRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ArticleRepositoryImpl @Inject constructor(
    private val lifeMashFirebaseService: LifeMashFirebaseService,
) : ArticleRepository {

    // default TTL: 5 minutes
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

    private suspend fun getArticles(
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

    override suspend fun searchArticles(
        query: String,
        category: String?,
        limit: Int,
    ): List<Article> {
        return withContext(Dispatchers.IO) {
            lifeMashFirebaseService
                .searchArticles(query = query, category = category, limit = limit)
                .mapNotNull { response ->
                    runCatching { response.toDomain() }.getOrNull()
                }
        }
    }

    private suspend fun fetchFromNetwork(category: ArticleCategory): List<Article> {
        return withContext(Dispatchers.IO) {
            lifeMashFirebaseService
                .getArticles(category = category.key)
                .mapNotNull { response ->
                    runCatching {
                        response.toDomain()
                    }.getOrNull()
                }
        }
    }
}