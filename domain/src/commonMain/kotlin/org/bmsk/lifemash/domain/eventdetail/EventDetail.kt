@file:OptIn(kotlin.time.ExperimentalTime::class)

package org.bmsk.lifemash.domain.eventdetail

import kotlin.time.Instant
import org.bmsk.lifemash.domain.calendar.EventTiming

data class EventDetail(
    val id: String,
    val groupId: String,
    val title: String,
    val description: String?,
    val timing: EventTiming,
    val location: String? = null,
    val imageEmoji: String = "",
    val sharedByNickname: String? = null,
    val attendees: List<EventAttendee> = emptyList(),
    val comments: List<EventComment> = emptyList(),
    val isJoined: Boolean = false,
)

data class EventAttendee(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)

data class EventComment(
    val id: String,
    val authorNickname: String,
    val content: String,
    val createdAt: Instant,
)
