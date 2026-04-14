package org.bmsk.lifemash.data.remote.notification.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: String,
    val userId: String,
    val type: String,
    val actorId: String? = null,
    val actorNickname: String? = null,
    val actorProfileImage: String? = null,
    val targetId: String? = null,
    val content: String? = null,
    val isRead: Boolean = false,
    val createdAt: String,
)

@Serializable
data class UnreadCountResponse(val count: Int)
