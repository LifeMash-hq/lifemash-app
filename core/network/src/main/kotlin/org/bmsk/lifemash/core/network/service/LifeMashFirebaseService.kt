package org.bmsk.lifemash.core.network.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.network.response.LifeMashArticleCategory
import org.bmsk.lifemash.core.network.response.LegacyLifeMashArticleResponse
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse
import javax.inject.Inject

interface LifeMashFirebaseService {
    suspend fun getLatestNews(
        limit: Int = 20,
        category: LifeMashCategory? = null,
    ): List<LegacyLifeMashArticleResponse>

    suspend fun getArticles(
        category: LifeMashArticleCategory,
        limit: Long = 20,
    ): List<LifeMashArticleResponse>
}

// LifeMash Firestore 호출용
internal class LifeMashFirebaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : LifeMashFirebaseService {

    override suspend fun getLatestNews(
        limit: Int,
        category: LifeMashCategory?
    ): List<LegacyLifeMashArticleResponse> {
        val safeLimit = limit.coerceAtLeast(1)

        var query: Query = db.collection(ARTICLES)

        if (category != null) {
            query = query.whereArrayContains(Fields.CATEGORY, category.id)
        }

        query = query
            .orderBy(Fields.PUBLISHED_TS, Query.Direction.DESCENDING)
            .limit(safeLimit.toLong())

        val snap = query.get().await()
        return snap.documents.mapNotNull {
            runCatching { it.toLifeMashArticle() }.getOrNull()
        }
    }

    override suspend fun getArticles(
        category: LifeMashArticleCategory,
        limit: Long
    ): List<LifeMashArticleResponse> {
        val query = db.collection(ARTICLES)
            .whereEqualTo("visible", true)
            .let { q ->
                if (category != LifeMashArticleCategory.ALL) {
                    q.whereArrayContains("categories", category.key)
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
                categories = (doc.get("categories") as? List<String>)
                    ?.map(LifeMashArticleCategory::fromKey)
                    .orEmpty(),
                visible = doc.getBoolean("visible") ?: true
            )
        }
    }

    private fun DocumentSnapshot.toLifeMashArticle(): LegacyLifeMashArticleResponse {
        val publishedTs = get(Fields.PUBLISHED_TS) as Timestamp
        return LegacyLifeMashArticleResponse(
            id = id,
            title = get(Fields.TITLE) as String,
            url = get(Fields.URL) as String,
            source = get(Fields.SOURCE) as String,
            pubDate = publishedTs.toDate(),
            summary = get(Fields.SUMMARY) as String,
            category = LifeMashCategory.fromId(
                (get(Fields.CATEGORY) as List<*>).filterIsInstance<String>().first()
            ),
            imageUrl = get(Fields.IMAGE_URL) as String
        )
    }

    private companion object {
        const val ARTICLES = "articles"

        object Fields {
            const val TITLE = "title"
            const val URL = "url"
            const val SOURCE = "source"
            const val PUBLISHED_TS = "published_ts"
            const val SUMMARY = "summary"
            const val CATEGORY = "categories"
            const val IMAGE_URL = "image_url"
        }
    }
}