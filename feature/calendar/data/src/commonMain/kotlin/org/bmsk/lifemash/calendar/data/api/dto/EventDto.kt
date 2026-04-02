package org.bmsk.lifemash.calendar.data.api.dto

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.EventVisibility

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
) {
    private fun toVisibility(): EventVisibility = when (visibility) {
        "public" -> EventVisibility.Public
        "followers" -> EventVisibility.Followers
        "group" -> EventVisibility.Group(visibilityGroupId ?: "")
        "specific" -> EventVisibility.Specific(visibilityUserIds ?: emptyList())
        "private" -> EventVisibility.Private
        else -> EventVisibility.Followers
    }

    fun toDomain() = Event(
        id = id,
        groupId = groupId,
        authorId = authorId,
        title = title,
        description = description,
        location = location,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        color = color,
        visibility = toVisibility(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

@Serializable
data class CreateEventBody(
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
data class UpdateEventBody(
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
