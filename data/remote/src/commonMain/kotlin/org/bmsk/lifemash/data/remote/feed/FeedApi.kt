package org.bmsk.lifemash.data.remote.feed

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.data.remote.feed.dto.FeedCommentDto
import org.bmsk.lifemash.data.remote.feed.dto.FeedPageDto
import org.bmsk.lifemash.data.remote.feed.dto.FeedPostDto

@Serializable
data class FeedResponseDto(val items: List<FeedPostDto>, val nextCursor: String? = null)

class FeedApi(private val client: HttpClient) {
    suspend fun getFeed(
        filterQueryValue: String,
        cursor: String?,
        limit: Int,
    ): FeedPageDto {
        val dto: FeedResponseDto = client.get("/api/v1/feed") {
            parameter("filter", filterQueryValue)
            cursor?.let { parameter("cursor", it) }
            parameter("limit", limit.toString())
        }.body()
        return FeedPageDto(items = dto.items, nextCursor = dto.nextCursor)
    }

    suspend fun like(postId: String): Unit = client.post("/api/v1/feed/$postId/like").body()
    suspend fun unlike(postId: String): Unit = client.delete("/api/v1/feed/$postId/like").body()

    suspend fun getComments(postId: String): List<FeedCommentDto> =
        client.get("/api/v1/feed/$postId/comments").body()

    suspend fun createComment(postId: String, content: String): FeedCommentDto =
        client.post("/api/v1/feed/$postId/comments") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body()
}
