package org.bmsk.lifemash.core.network.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.core.network.response.SearchRequestBody
import org.bmsk.lifemash.core.network.response.SearchRequestData
import javax.inject.Inject

interface LifeMashFirebaseService {
    suspend fun getArticles(
        category: String,
        limit: Long = 20,
    ): List<LifeMashArticleResponse>

    suspend fun searchArticles(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<LifeMashArticleResponse>
}

// LifeMash Firestore 호출용
internal class LifeMashFirebaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val searchService: SearchService,
) : LifeMashFirebaseService {

    override suspend fun getArticles(
        category: String,
        limit: Long
    ): List<LifeMashArticleResponse> {
        val isAll = category == "_all_"
        val query = db.collection(ARTICLES)
            .whereEqualTo("visible", true)
            .let { q ->
                if (!isAll) {
                    q.whereArrayContains("categories", category)
                } else {
                    q
                }
            }
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .limit(limit)

        val snapshot = query.get().await()

        @Suppress("UNCHECKED_CAST")
        return snapshot.documents.map { doc ->
            LifeMashArticleResponse(
                id = doc.id,
                publisher = doc.getString("publisher"),
                title = doc.getString("title"),
                summary = doc.getString("summary"),
                link = doc.getString("link"),
                image = doc.getString("image"),
                publishedAt = doc.getLong("publishedAt"),
                host = doc.getString("host"),
                categories = (doc.get("categories") as? List<String>).orEmpty(),
                visible = doc.getBoolean("visible") ?: true
            )
        }
    }

    override suspend fun searchArticles(
        query: String,
        category: String?,
        limit: Int,
    ): List<LifeMashArticleResponse> = withContext(Dispatchers.IO) {
        val response = searchService.search(
            SearchRequestBody(
                data = SearchRequestData(
                    query = query,
                    category = category,
                    limit = limit,
                )
            )
        )

        response.result.articles.map { article ->
            LifeMashArticleResponse(
                id = article.id,
                publisher = article.publisher,
                title = article.title,
                summary = article.summary,
                link = article.link,
                image = article.image,
                publishedAt = article.publishedAt,
                host = article.host,
                categories = article.categories,
                visible = article.visible,
            )
        }
    }

    private companion object {
        const val ARTICLES = "articles"
    }
}
