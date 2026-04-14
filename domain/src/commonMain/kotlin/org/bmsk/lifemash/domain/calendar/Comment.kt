package org.bmsk.lifemash.domain.calendar

import kotlin.time.Instant

data class Comment(
    val id: String,
    val eventId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
)
