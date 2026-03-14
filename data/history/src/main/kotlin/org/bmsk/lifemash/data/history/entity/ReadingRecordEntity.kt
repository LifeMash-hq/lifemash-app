package org.bmsk.lifemash.data.history.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "reading_history")
data class ReadingRecordEntity(
    @PrimaryKey val articleId: String,
    val readAt: Instant,
)
