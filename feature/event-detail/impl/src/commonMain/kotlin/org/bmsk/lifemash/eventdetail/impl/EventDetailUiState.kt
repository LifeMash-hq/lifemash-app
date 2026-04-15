package org.bmsk.lifemash.eventdetail.impl

import kotlin.time.Instant
import org.bmsk.lifemash.domain.calendar.EventTiming

data class Comment(
    val id: String,
    val authorNickname: String,
    val content: String,
    val createdAt: Instant,
)

data class Attendee(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)

sealed interface EventDetailUiState {
    data object Loading : EventDetailUiState
    data class Loaded(
        val eventId: String,
        val title: String,
        val date: String,
        val timing: EventTiming,
        val location: String? = null,
        val description: String? = null,
        val imageEmoji: String = "",
        val sharedByNickname: String? = null,
        val attendees: List<Attendee> = emptyList(),
        val comments: List<Comment> = emptyList(),
        val isJoined: Boolean = false,
    ) : EventDetailUiState
    data class Error(val message: String) : EventDetailUiState
}
