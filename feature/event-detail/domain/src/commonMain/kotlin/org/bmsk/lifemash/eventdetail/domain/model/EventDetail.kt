@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class EventDetail(
    val id: String,
    val groupId: String,
    val title: String,
    val description: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val location: String? = null,
    val imageEmoji: String = "",
    val sharedByNickname: String? = null,
    val attendees: List<EventAttendee> = emptyList(),
    val comments: List<EventComment> = emptyList(),
    val isJoined: Boolean = false,
)

@Serializable
data class EventAttendee(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)

@Serializable
data class EventComment(
    val id: String,
    val authorNickname: String,
    val content: String,
    val createdAt: Instant,
)
