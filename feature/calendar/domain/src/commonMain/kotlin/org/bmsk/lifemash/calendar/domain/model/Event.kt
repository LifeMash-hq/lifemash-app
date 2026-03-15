package org.bmsk.lifemash.calendar.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Event(
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
)
