package org.bmsk.lifemash.core.network.service

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse
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
    private val db: FirebaseFirestore
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
        val functions = Firebase.functions(REGION)
        val callable = functions.getHttpsCallable("searchArticles")

        val params = buildMap<String, Any> {
            put("query", query)
            if (category != null) put("category", category)
            put("limit", limit)
        }

        @Suppress("UNCHECKED_CAST")
        val result = callable.call(params).await().data as Map<String, Any?>

        @Suppress("UNCHECKED_CAST")
        val articles = (result["articles"] as? List<Map<String, Any?>>).orEmpty()

        articles.map { article ->
            @Suppress("UNCHECKED_CAST")
            LifeMashArticleResponse(
                id = article["id"] as? String ?: "",
                publisher = article["publisher"] as? String,
                title = article["title"] as? String,
                summary = article["summary"] as? String,
                link = article["link"] as? String,
                image = article["image"] as? String,
                publishedAt = (article["publishedAt"] as? Number)?.toLong(),
                host = article["host"] as? String,
                categories = (article["categories"] as? List<String>).orEmpty(),
                visible = article["visible"] as? Boolean ?: true,
            )
        }
    }

    private companion object {
        const val REGION = "asia-northeast3"
        const val ARTICLES = "articles"
    }
}
