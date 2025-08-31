package org.bmsk.lifemash.data.scrap.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.bmsk.lifemash.data.scrap.dao.ScrapArticleDao
import org.bmsk.lifemash.data.scrap.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class ScrapArticleDB : RoomDatabase() {
    abstract fun articleDao(): ScrapArticleDao
}
