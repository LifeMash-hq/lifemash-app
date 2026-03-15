package org.bmsk.lifemash.calendar.data.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.calendar.domain.model.Event

@Serializable
data class EventDto(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val description: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val color: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun toDomain() = Event(
        id = id,
        groupId = groupId,
        authorId = authorId,
        title = title,
        description = description,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

@Serializable
data class CreateEventBody(
    val title: String,
    val description: String?,
    val startAt: Instant,
    val endAt: Instant?,
    val isAllDay: Boolean,
    val color: String?,
)

@Serializable
data class UpdateEventBody(
    val title: String?,
    val description: String?,
    val startAt: Instant?,
    val endAt: Instant?,
    val isAllDay: Boolean?,
    val color: String?,
)
