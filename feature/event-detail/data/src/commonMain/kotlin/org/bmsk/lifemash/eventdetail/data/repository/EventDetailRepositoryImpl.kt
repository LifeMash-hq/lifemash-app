package org.bmsk.lifemash.eventdetail.data.repository

import org.bmsk.lifemash.eventdetail.data.api.EventDetailApi
import org.bmsk.lifemash.eventdetail.domain.model.EventAttendee
import org.bmsk.lifemash.eventdetail.domain.model.EventComment
import org.bmsk.lifemash.eventdetail.domain.model.EventDetail
import org.bmsk.lifemash.eventdetail.domain.repository.EventDetailRepository
import org.bmsk.lifemash.model.calendar.CreateCommentRequest

internal class EventDetailRepositoryImpl(
    private val api: EventDetailApi
) : EventDetailRepository {

    override suspend fun getEventDetail(eventId: String): EventDetail {
        val dto = api.getEventDetail(eventId)
        return EventDetail(
            id = dto.id,
            groupId = dto.groupId,
            title = dto.title,
            description = dto.description,
            startAt = dto.startAt,
            endAt = dto.endAt,
            location = dto.location,
            imageEmoji = dto.imageEmoji ?: "",
            sharedByNickname = dto.authorNickname,
            attendees = dto.attendees.map { attendeeDto ->
                EventAttendee(
                    id = attendeeDto.id,
                    nickname = attendeeDto.nickname,
                    profileImage = attendeeDto.profileImage
                )
            },
            comments = dto.comments.map { commentDto ->
                EventComment(
                    id = commentDto.id,
                    authorNickname = commentDto.authorNickname,
                    content = commentDto.content,
                    createdAt = commentDto.createdAt
                )
            },
            isJoined = dto.isJoined
        )
    }

    override suspend fun toggleJoin(eventId: String): Boolean {
        return api.toggleJoin(eventId).isJoined
    }

    override suspend fun addComment(eventId: String, content: String): EventComment {
        val dto = api.createComment(eventId, CreateCommentRequest(content))
        return EventComment(
            id = dto.id,
            authorNickname = dto.authorNickname,
            content = dto.content,
            createdAt = dto.createdAt
        )
    }
}
