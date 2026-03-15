package org.bmsk.lifemash.feed.data.history.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getReadingHistoryDBBuilder(): RoomDatabase.Builder<ReadingHistoryDB> {
    val dbFilePath = NSHomeDirectory() + "/Documents/reading_history.db"
    return Room.databaseBuilder<ReadingHistoryDB>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver())
}
