package org.bmsk.lifemash.data.remote.calendar

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.calendar.dto.CommentDto
import org.bmsk.lifemash.data.remote.calendar.dto.CreateCommentRequest
import org.bmsk.lifemash.data.remote.calendar.dto.CreateEventRequest
import org.bmsk.lifemash.data.remote.calendar.dto.CreateGroupRequest
import org.bmsk.lifemash.data.remote.calendar.dto.EventDto
import org.bmsk.lifemash.data.remote.calendar.dto.GroupDto
import org.bmsk.lifemash.data.remote.calendar.dto.JoinGroupRequest
import org.bmsk.lifemash.data.remote.calendar.dto.UpdateEventRequest
import org.bmsk.lifemash.data.remote.calendar.dto.UpdateGroupNameRequest

class CalendarApi(private val client: HttpClient) {

    private val base = "/api/v1/calendar"

    suspend fun getMonthEvents(
        groupId: String,
        year: Int,
        month: Int,
    ): List<EventDto> =
        client.get("$base/$groupId/events") {
            url.parameters.append("year", year.toString())
            url.parameters.append("month", month.toString())
        }.body()

    suspend fun createEvent(groupId: String, body: CreateEventRequest): EventDto =
        client.post("$base/$groupId/events") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun updateEvent(
        groupId: String,
        eventId: String,
        body: UpdateEventRequest,
    ): EventDto =
        client.patch("$base/$groupId/events/$eventId") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun deleteEvent(groupId: String, eventId: String): Unit =
        client.delete("$base/$groupId/events/$eventId").body()

    suspend fun createGroup(body: CreateGroupRequest): GroupDto =
        client.post("$base/groups") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun joinGroup(body: JoinGroupRequest): GroupDto =
        client.post("$base/groups/join") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun getMyGroups(): List<GroupDto> =
        client.get("$base/groups").body()

    suspend fun getGroup(groupId: String): GroupDto =
        client.get("$base/groups/$groupId").body()

    suspend fun deleteGroup(groupId: String): Unit =
        client.delete("$base/groups/$groupId").body()

    suspend fun updateGroupName(groupId: String, body: UpdateGroupNameRequest): GroupDto =
        client.patch("$base/groups/$groupId") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun getComments(groupId: String, eventId: String): List<CommentDto> =
        client.get("$base/$groupId/events/$eventId/comments").body()

    suspend fun createComment(
        groupId: String,
        eventId: String,
        body: CreateCommentRequest,
    ): CommentDto =
        client.post("$base/$groupId/events/$eventId/comments") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun deleteComment(
        groupId: String,
        eventId: String,
        commentId: String,
    ): Unit =
        client.delete("$base/$groupId/events/$eventId/comments/$commentId").body()
}
