package org.bmsk.lifemash.core.repo.scrap.impl.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.bmsk.lifemash.core.repo.scrap.impl.DateConverter
import org.bmsk.lifemash.core.repo.scrap.impl.dao.ScrapNewsDao
import org.bmsk.lifemash.core.repo.scrap.impl.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = ScrapNewsDB.SCRAP_NEWS_DB_VERSION)
@TypeConverters(DateConverter::class)
internal abstract class ScrapNewsDB: RoomDatabase() {
    abstract fun newsDao(): ScrapNewsDao

    companion object {
        const val SCRAP_NEWS_DB_VERSION = 2
        const val SCRAP_NEWS_DB_NAME = "scrap_news_db"
    }
}
