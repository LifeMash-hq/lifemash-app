package org.bmsk.lifemash.notification.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getNotificationKeywordDBBuilder(context: Context): RoomDatabase.Builder<NotificationKeywordDB> {
    val dbFile = context.getDatabasePath("notification_keywords.db")
    return Room.databaseBuilder<NotificationKeywordDB>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
