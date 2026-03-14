package org.bmsk.lifemash.data.history.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.bmsk.lifemash.data.history.dao.ReadingHistoryDao
import org.bmsk.lifemash.data.history.entity.ReadingRecordEntity

@Database(entities = [ReadingRecordEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class ReadingHistoryDB : RoomDatabase() {
    abstract fun readingHistoryDao(): ReadingHistoryDao
}
