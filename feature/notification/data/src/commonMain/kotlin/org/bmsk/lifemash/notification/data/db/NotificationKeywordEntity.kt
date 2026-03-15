package org.bmsk.lifemash.notification.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_keywords")
data class NotificationKeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val createdAt: Long,    // epoch millis (kotlinx.datetime.Instant.toEpochMilliseconds())
)
