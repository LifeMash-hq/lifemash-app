package org.bmsk.lifemash.model.calendar

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val description: String? = null,
    val startAt: Instant,
    val endAt: Instant? = null,
    val isAllDay: Boolean = false,
    val color: String? = null,
    val location: String? = null,
    val imageEmoji: String? = null,
    val visibility: String = "followers",
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val startAt: Instant,
    val endAt: Instant? = null,
    val isAllDay: Boolean = false,
    val color: String? = null,
    val visibility: String = "followers",
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
)

@Serializable
data class UpdateEventRequest(
    val title: String? = null,
    val description: String? = null,
    val startAt: Instant? = null,
    val endAt: Instant? = null,
    val isAllDay: Boolean? = null,
    val color: String? = null,
    val location: String? = null,
    val imageEmoji: String? = null,
    val visibility: String? = null,
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
)
