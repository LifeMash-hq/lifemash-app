package org.bmsk.lifemash.feed.data.history.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bmsk.lifemash.model.ArticleCategory
import kotlin.time.Instant

@Entity(tableName = "reading_history")
data class ReadingRecordEntity(
    @PrimaryKey val articleId: String,
    val readAt: Instant,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: String,
    val image: String?,
    val publishedAt: Instant,
    val categories: List<ArticleCategory>,
)
