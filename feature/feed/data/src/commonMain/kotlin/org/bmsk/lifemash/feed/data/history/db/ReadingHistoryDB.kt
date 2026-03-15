package org.bmsk.lifemash.feed.data.history.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.bmsk.lifemash.feed.data.history.entity.ReadingRecordEntity

@Database(entities = [ReadingRecordEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
@ConstructedBy(ReadingHistoryDBConstructor::class)
abstract class ReadingHistoryDB : RoomDatabase() {
    abstract fun readingHistoryDao(): org.bmsk.lifemash.feed.data.history.dao.ReadingHistoryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ReadingHistoryDBConstructor : RoomDatabaseConstructor<ReadingHistoryDB>
