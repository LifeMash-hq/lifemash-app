package org.bmsk.lifemash.scrap.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getScrapArticleDBBuilder(): RoomDatabase.Builder<ScrapArticleDB> {
    val dbFilePath = NSHomeDirectory() + "/Documents/scrap_article_db"
    return Room.databaseBuilder<ScrapArticleDB>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver())
}
