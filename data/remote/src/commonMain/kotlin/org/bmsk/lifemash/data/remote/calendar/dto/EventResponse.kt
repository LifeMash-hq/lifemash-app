package org.bmsk.lifemash.data.remote.calendar.dto

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val description: String?,
    val location: String? = null,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val color: String?,
    val visibility: String = "followers",
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String?,
    val location: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val color: String?,
    val visibility: String = "followers",
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
)

@Serializable
data class UpdateEventRequest(
    val title: String?,
    val description: String?,
    val location: String?,
    val startAt: Instant?,
    val endAt: Instant?,
    val isAllDay: Boolean?,
    val color: String?,
    val visibility: String? = null,
    val visibilityGroupId: String? = null,
    val visibilityUserIds: List<String>? = null,
)
