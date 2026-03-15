package org.bmsk.lifemash.feed.data.history.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getReadingHistoryDBBuilder(context: Context): RoomDatabase.Builder<ReadingHistoryDB> {
    val dbFile = context.getDatabasePath("reading_history.db")
    return Room.databaseBuilder<ReadingHistoryDB>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
