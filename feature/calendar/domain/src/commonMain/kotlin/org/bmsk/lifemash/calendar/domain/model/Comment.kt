package org.bmsk.lifemash.calendar.domain.model

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val eventId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
)
