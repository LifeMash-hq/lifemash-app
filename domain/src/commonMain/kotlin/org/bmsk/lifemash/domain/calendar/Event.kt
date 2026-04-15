package org.bmsk.lifemash.domain.calendar

import kotlin.time.Instant

data class Event(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val timing: EventTiming,
    val color: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val visibility: EventVisibility = EventVisibility.Followers,
)
