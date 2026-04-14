package org.bmsk.lifemash.data.remote.eventdetail.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class EventAttendeeResponse(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
    val status: String = "attending",
)

@Serializable
data class EventDetailResponse(
    val id: String,
    val groupId: String,
    val title: String,
    val description: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val location: String?,
    val imageEmoji: String?,
    val authorNickname: String?,
    val attendees: List<EventAttendeeResponse>,
    val comments: List<CommentResponse>,
    val isJoined: Boolean,
)

@Serializable
data class ToggleJoinResponse(
    val isJoined: Boolean,
)
