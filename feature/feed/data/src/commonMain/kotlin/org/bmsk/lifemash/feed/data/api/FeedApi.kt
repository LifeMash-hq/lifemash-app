package org.bmsk.lifemash.feed.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.feed.domain.model.FeedComment
import org.bmsk.lifemash.feed.domain.model.FeedFilter
import org.bmsk.lifemash.feed.domain.model.FeedPost
import org.bmsk.lifemash.feed.domain.repository.FeedPage

@Serializable
data class FeedResponseDto(val items: List<FeedPost>, val nextCursor: String? = null)

internal class FeedApi(private val client: HttpClient) {
    suspend fun getFeed(filter: FeedFilter, cursor: String?, limit: Int): FeedPage {
        val dto: FeedResponseDto = client.get("/api/v1/feed") {
            parameter("filter", filter.queryValue)
            cursor?.let { parameter("cursor", it) }
            parameter("limit", limit.toString())
        }.body()
        return FeedPage(items = dto.items, nextCursor = dto.nextCursor)
    }

    suspend fun like(postId: String): Unit = client.post("/api/v1/feed/$postId/like").body()
    suspend fun unlike(postId: String): Unit = client.delete("/api/v1/feed/$postId/like").body()

    suspend fun getComments(postId: String): List<FeedComment> =
        client.get("/api/v1/feed/$postId/comments").body()

    suspend fun createComment(postId: String, content: String): FeedComment =
        client.post("/api/v1/feed/$postId/comments") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body()
}
