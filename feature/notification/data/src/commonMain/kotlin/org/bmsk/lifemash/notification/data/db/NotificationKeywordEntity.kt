package org.bmsk.lifemash.notification.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notification_keywords",
    indices = [Index(value = ["keyword"], unique = true)],
)
data class NotificationKeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val createdAt: Long,
)
