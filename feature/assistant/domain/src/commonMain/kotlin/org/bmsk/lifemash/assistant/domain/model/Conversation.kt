package org.bmsk.lifemash.assistant.domain.model

import kotlinx.datetime.Instant

data class Conversation(
    val id: String,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
