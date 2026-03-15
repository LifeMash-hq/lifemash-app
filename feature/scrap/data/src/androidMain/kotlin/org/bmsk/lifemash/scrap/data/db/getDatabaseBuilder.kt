package org.bmsk.lifemash.scrap.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getScrapArticleDBBuilder(context: Context): RoomDatabase.Builder<ScrapArticleDB> {
    val dbFile = context.getDatabasePath("scrap_article_db")
    return Room.databaseBuilder<ScrapArticleDB>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
