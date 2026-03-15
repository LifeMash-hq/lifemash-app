package org.bmsk.lifemash.scrap.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.bmsk.lifemash.scrap.data.dao.ScrapArticleDao
import org.bmsk.lifemash.scrap.data.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
@ConstructedBy(ScrapArticleDBConstructor::class)
abstract class ScrapArticleDB : RoomDatabase() {
    abstract fun articleDao(): ScrapArticleDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ScrapArticleDBConstructor : RoomDatabaseConstructor<ScrapArticleDB>
