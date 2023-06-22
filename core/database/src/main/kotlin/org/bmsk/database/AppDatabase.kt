package org.bmsk.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.bmsk.database.converter.DateConverter
import org.bmsk.database.dao.NewsDao
import org.bmsk.database.model.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun newsDao(): NewsDao
}