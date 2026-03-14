package org.bmsk.lifemash.core.network.service

import com.algolia.client.exception.AlgoliaApiException
import com.algolia.client.model.search.SearchForHits
import com.algolia.client.model.search.SearchMethodParams
import com.algolia.client.model.search.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.bmsk.lifemash.core.network.response.LifeMashArticleResponse

interface LifeMashAlgoliaService {
    suspend fun search(query: String): List<LifeMashArticleResponse>
}

internal class LifeMashAlgoliaServiceImpl(
    private val algoliaClientProvider: AlgoliaClientProvider = FirebaseAlgoliaClientProvider()
) : LifeMashAlgoliaService {
    override suspend fun search(query: String): List<LifeMashArticleResponse> = Dispatchers.IO {
        runCatching {
            val clientInfo = algoliaClientProvider.getClientInfo(forceRefresh = false)
            doSearch(query, clientInfo)
        }.recoverCatching { t ->
            if (t is AlgoliaApiException) {
                val msg = t.message?.lowercase().orEmpty()
                val isAuthOrExpiryError =
                    t.httpErrorCode == 401
                            || t.httpErrorCode == 403
                            || "expired" in msg
                            || "invalid api key" in msg
                            || "unauthorized" in msg

                if (isAuthOrExpiryError) {
                    val clientInfo = algoliaClientProvider.getClientInfo(forceRefresh = true)
                    return@recoverCatching doSearch(query, clientInfo) // 여기서부터 발생하는 예외는 외부로
                }
            }
            throw t
        }.getOrThrow()
    }

    private suspend fun doSearch(
        query: String,
        clientInfo: AlgoliaClientInfo
    ): List<LifeMashArticleResponse> {
        val responses = clientInfo.client.search(
            SearchMethodParams(
                requests = listOf(SearchForHits(indexName = clientInfo.indexName, query = query))
            )
        )

        return responses.results
            .asSequence()
            .filterIsInstance<SearchResponse>()
            .flatMap { it.hits.asSequence() }
            .mapNotNull { hit ->
                val props: Map<String, JsonElement> =
                    hit.additionalProperties ?: return@mapNotNull null

                LifeMashArticleResponse(
                    id = hit.objectID,
                    publisher = props["publisher"].string(),
                    title = props["title"].string(),
                    summary = props["summary"].string(),
                    link = props["link"].string(),
                    image = props["image"].string(),
                    publishedAt = props["publishedAt"].long(),
                    host = props["host"].string(),
                    categories = props["categories"].stringList().orEmpty(),
                    visible = props["visible"].bool() ?: true
                )
            }
            .toList()
    }
}

private fun JsonElement?.string(): String? =
    this?.jsonPrimitive?.contentOrNull

private fun JsonElement?.long(): Long? =
    this?.jsonPrimitive?.longOrNull

private fun JsonElement?.bool(): Boolean? =
    this?.jsonPrimitive?.booleanOrNull

private fun JsonElement?.stringList(): List<String>? =
    (this as? JsonArray)?.mapNotNull { it.jsonPrimitive.contentOrNull }