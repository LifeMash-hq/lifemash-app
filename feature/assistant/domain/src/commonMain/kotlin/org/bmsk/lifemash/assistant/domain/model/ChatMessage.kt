package org.bmsk.lifemash.assistant.domain.model

import kotlinx.datetime.Instant

data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val createdAt: Instant,
)
