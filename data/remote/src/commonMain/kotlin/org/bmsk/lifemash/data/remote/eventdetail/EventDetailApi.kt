package org.bmsk.lifemash.data.remote.eventdetail

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.eventdetail.dto.CommentResponse
import org.bmsk.lifemash.data.remote.eventdetail.dto.CreateCommentRequest
import org.bmsk.lifemash.data.remote.eventdetail.dto.EventDetailResponse
import org.bmsk.lifemash.data.remote.eventdetail.dto.ToggleJoinResponse

class EventDetailApi(private val client: HttpClient) {

    private val base = "/api/v1/events"

    suspend fun getEventDetail(eventId: String): EventDetailResponse =
        client.get("$base/$eventId").body()

    suspend fun toggleJoin(eventId: String): ToggleJoinResponse =
        client.post("$base/$eventId/join").body()

    suspend fun createComment(eventId: String, request: CreateCommentRequest): CommentResponse =
        client.post("$base/$eventId/comments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
