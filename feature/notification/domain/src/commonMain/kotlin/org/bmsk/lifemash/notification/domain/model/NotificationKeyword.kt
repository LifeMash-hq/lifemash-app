package org.bmsk.lifemash.notification.domain.model

import kotlinx.datetime.Instant

data class NotificationKeyword(
    val id: Long = 0,
    val keyword: String,
    val createdAt: Instant,
)
