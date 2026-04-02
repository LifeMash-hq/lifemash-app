package org.bmsk.lifemash.eventdetail.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.ToggleJoinResponse

internal class EventDetailApi(private val client: HttpClient) {

    private val base = "/api/v1/events"

    suspend fun getEventDetail(eventId: String): EventDetailDto =
        client.get("$base/$eventId").body()

    suspend fun toggleJoin(eventId: String): ToggleJoinResponse =
        client.post("$base/$eventId/join").body()

    suspend fun createComment(eventId: String, request: CreateCommentRequest): CommentDto =
        client.post("$base/$eventId/comments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
