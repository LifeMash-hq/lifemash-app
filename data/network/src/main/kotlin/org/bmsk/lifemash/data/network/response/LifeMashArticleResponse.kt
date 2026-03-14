package org.bmsk.lifemash.data.network.response

data class LifeMashArticleResponse(
    val id: String = "",
    val publisher: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val link: String? = null,
    val image: String? = null,
    val publishedAt: Long? = null,
    val host: String? = null,
    val categories: List<String> = emptyList(),
    val visible: Boolean = true
)
