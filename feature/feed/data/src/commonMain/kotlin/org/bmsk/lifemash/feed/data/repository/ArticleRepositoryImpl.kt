package org.bmsk.lifemash.feed.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.bmsk.lifemash.data.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.feed.data.model.toDomain
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository
import org.bmsk.lifemash.model.Article
import org.bmsk.lifemash.model.ArticleCategory

class ArticleRepositoryImpl(
    private val lifeMashFirebaseService: LifeMashFirebaseService,
) : ArticleRepository {

    // default TTL: 5 minutes
    private val defaultTtlMillis: Long = 5 * 60 * 1000L

    private data class Cached(
        val data: List<Article>,
        val fetchedAt: Long
    )

    private val cacheMutex = Mutex()
    private val cache = HashMap<ArticleCategory, Cached>()
    private val mutexMap = HashMap<ArticleCategory, Mutex>()

    override suspend fun getArticles(
        category: ArticleCategory
    ): List<Article> = getArticles(category, ttlMillis = defaultTtlMillis, forceRefresh = false)

    private suspend fun getArticles(
        category: ArticleCategory,
        ttlMillis: Long = defaultTtlMillis,
        forceRefresh: Boolean = false
    ): List<Article> {
        val mutex = cacheMutex.withLock {
            mutexMap.getOrPut(category) { Mutex() }
        }

        return mutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            val cached = cacheMutex.withLock { cache[category] }
            val isFresh = cached != null && (now - cached.fetchedAt) <= ttlMillis

            if (!forceRefresh && isFresh) {
                return@withLock cached.data
            }

            val fresh = fetchFromNetwork(category)
            cacheMutex.withLock {
                cache[category] = Cached(fresh, now)
            }

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
