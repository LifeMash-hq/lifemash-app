package org.bmsk.lifemash.feed.data.history.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.feed.data.history.dao.ReadingHistoryDao
import org.bmsk.lifemash.feed.data.history.db.ReadingHistoryDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ReadingHistoryDBModule {
    @Provides
    fun provideReadingHistoryDao(
        db: ReadingHistoryDB
    ): ReadingHistoryDao = db.readingHistoryDao()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ReadingHistoryDB = Room.databaseBuilder(
        context = context,
        klass = ReadingHistoryDB::class.java,
        name = "reading_history_db"
    ).build()
}
