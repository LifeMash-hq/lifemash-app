package org.bmsk.lifemash.notification.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getNotificationKeywordDBBuilder(): RoomDatabase.Builder<NotificationKeywordDB> {
    val dbFilePath = NSHomeDirectory() + "/Documents/notification_keywords.db"
    return Room.databaseBuilder<NotificationKeywordDB>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver())
}
