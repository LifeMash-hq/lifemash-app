package org.bmsk.lifemash.scrap.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.bmsk.lifemash.scrap.data.dao.ScrapArticleDao
import org.bmsk.lifemash.scrap.data.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class ScrapArticleDB : RoomDatabase() {
    abstract fun articleDao(): ScrapArticleDao
}
