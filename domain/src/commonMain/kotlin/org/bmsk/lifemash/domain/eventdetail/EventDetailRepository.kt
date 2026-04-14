package org.bmsk.lifemash.domain.eventdetail

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: String): EventDetail
    suspend fun toggleJoin(eventId: String): Boolean
    suspend fun addComment(eventId: String, content: String): EventComment
}
