package org.bmsk.lifemash.core.network.service

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.network.response.LifeMashArticle
import javax.inject.Inject

interface LifeMashFirebaseService {
    suspend fun getLatestNews(
        limit: Int = 20,
        category: LifeMashCategory? = null,
    ): List<LifeMashArticle>
}

internal class LifeMashFirebaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : LifeMashFirebaseService {

    override suspend fun getLatestNews(
        limit: Int,
        category: LifeMashCategory?
    ): List<LifeMashArticle> {
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
            runCatching { it.toLifeMashArticle() }.onFailure {
                Log.e(
                    "ddd",
                    it.stackTraceToString()
                )
            }.getOrNull()
        }
    }

    private fun DocumentSnapshot.toLifeMashArticle(): LifeMashArticle {
        val publishedTs = get(Fields.PUBLISHED_TS) as Timestamp
        return LifeMashArticle(
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