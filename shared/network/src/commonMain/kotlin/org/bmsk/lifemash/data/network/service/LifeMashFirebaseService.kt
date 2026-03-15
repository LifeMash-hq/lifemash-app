package org.bmsk.lifemash.data.network.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import org.bmsk.lifemash.data.network.response.LifeMashArticleResponse
import org.bmsk.lifemash.data.network.response.SearchRequestBody
import org.bmsk.lifemash.data.network.response.SearchRequestData

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

internal class LifeMashFirebaseServiceImpl(
    private val searchService: SearchService,
) : LifeMashFirebaseService {

    private val db = Firebase.firestore

    override suspend fun getArticles(
        category: String,
        limit: Long,
    ): List<LifeMashArticleResponse> {
        val isAll = category == "_all_"
        var query = db.collection(ARTICLES)
            .where { "visible" equalTo true }

        if (!isAll) {
            query = query.where { "categories" contains category }
        }

        val snapshot = query
            .orderBy("publishedAt", Direction.DESCENDING)
            .limit(limit.toInt())
            .get()

        return snapshot.documents.map { doc ->
            LifeMashArticleResponse(
                id = doc.id,
                publisher = doc.get<String?>("publisher"),
                title = doc.get<String?>("title"),
                summary = doc.get<String?>("summary"),
                link = doc.get<String?>("link"),
                image = doc.get<String?>("image"),
                publishedAt = doc.get<Long?>("publishedAt"),
                host = doc.get<String?>("host"),
                categories = doc.get<List<String>?>("categories").orEmpty(),
                visible = doc.get<Boolean?>("visible") ?: true,
            )
        }
    }

    override suspend fun searchArticles(
        query: String,
        category: String?,
        limit: Int,
    ): List<LifeMashArticleResponse> {
        val response = searchService.search(
            SearchRequestBody(
                data = SearchRequestData(
                    query = query,
                    category = category,
                    limit = limit,
                )
            )
        )

        return response.result.articles.map { article ->
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
