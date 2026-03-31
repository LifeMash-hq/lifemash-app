package org.bmsk.lifemash.eventdetail.domain.repository

import org.bmsk.lifemash.eventdetail.domain.model.EventComment
import org.bmsk.lifemash.eventdetail.domain.model.EventDetail

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: String): EventDetail
    suspend fun toggleJoin(eventId: String): Boolean
    suspend fun addComment(eventId: String, content: String): EventComment
}
