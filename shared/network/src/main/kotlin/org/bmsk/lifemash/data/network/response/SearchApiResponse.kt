package org.bmsk.lifemash.data.network.response

import kotlinx.serialization.Serializable

@Serializable
internal data class SearchRequestData(
    val query: String,
    val category: String? = null,
    val limit: Int = 20,
)

@Serializable
internal data class SearchRequestBody(
    val data: SearchRequestData,
)

@Serializable
internal data class SearchResultBody(
    val articles: List<SearchArticle> = emptyList(),
    val count: Int = 0,
)

@Serializable
internal data class SearchArticle(
    val id: String = "",
    val publisher: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val link: String? = null,
    val image: String? = null,
    val publishedAt: Long? = null,
    val host: String? = null,
    val categories: List<String> = emptyList(),
    val visible: Boolean = true,
)

@Serializable
internal data class SearchApiResponse(
    val result: SearchResultBody,
)
