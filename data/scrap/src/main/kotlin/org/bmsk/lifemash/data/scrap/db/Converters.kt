package org.bmsk.lifemash.data.scrap.db

import androidx.room.TypeConverter
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import java.time.Instant

internal class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
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
