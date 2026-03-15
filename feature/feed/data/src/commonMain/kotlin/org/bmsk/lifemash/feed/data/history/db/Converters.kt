package org.bmsk.lifemash.feed.data.history.db

import androidx.room.TypeConverter
import org.bmsk.lifemash.model.ArticleCategory
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

    @TypeConverter
    fun fromCategories(value: String?): List<ArticleCategory> {
        return value?.split(',')?.map { ArticleCategory.valueOf(it) } ?: emptyList()
    }

    @TypeConverter
    fun toCategories(categories: List<ArticleCategory>?): String? {
        return categories?.joinToString(",") { it.name }
    }
}
