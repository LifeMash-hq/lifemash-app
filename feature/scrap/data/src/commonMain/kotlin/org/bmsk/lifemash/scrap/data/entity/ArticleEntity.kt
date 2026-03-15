package org.bmsk.lifemash.scrap.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bmsk.lifemash.model.ArticleCategory
import kotlinx.datetime.Instant

@Entity(tableName = "scrap_articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val publisher: String,
    val title: String,
    val summary: String,
    val link: String,
    val image: String?,
    val publishedAt: Instant,
    val categories: List<ArticleCategory>
)
