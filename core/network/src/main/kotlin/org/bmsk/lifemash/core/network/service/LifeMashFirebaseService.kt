package org.bmsk.lifemash.core.network.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.bmsk.lifemash.core.model.section.LifeMashSection
import org.bmsk.lifemash.core.network.response.LifeMashArticle
import javax.inject.Inject

interface LifeMashFirebaseService {
    suspend fun getLatestNews(
        limit: Int = 20,
        section: LifeMashSection? = null,
    ): List<LifeMashArticle>
}

internal class LifeMashFirebaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : LifeMashFirebaseService {

    override suspend fun getLatestNews(
        limit: Int,
        section: LifeMashSection?
    ): List<LifeMashArticle> {
        val safeLimit = limit.coerceAtLeast(1)

        val query: Query = db.collection(ARTICLES)
            .let { base ->
                if (section != null) {
                    base.whereEqualTo(Fields.CATEGORY, section.id)
                } else {
                    base
                }
            }
            .orderBy(Fields.PUBLISHED, Query.Direction.DESCENDING)
            .limit(safeLimit.toLong())

        val snap = query.get().await()
        return snap
            .documents
            .mapNotNull {
                runCatching { it.toLifeMashArticle() }.getOrNull()
            }
    }

    private fun DocumentSnapshot.toLifeMashArticle(): LifeMashArticle {
        return LifeMashArticle(
            id = id,
            title = get(Fields.TITLE) as String,
            url = get(Fields.URL) as String,
            source = get(Fields.SOURCE) as String,
            published = get(Fields.PUBLISHED) as String,
            summary = get(Fields.SUMMARY) as String,
            section = LifeMashSection.fromId(
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
            const val PUBLISHED = "published"
            const val SUMMARY = "summary"
            const val CATEGORY = "categories"
            const val IMAGE_URL = "image_url"
        }
    }
}