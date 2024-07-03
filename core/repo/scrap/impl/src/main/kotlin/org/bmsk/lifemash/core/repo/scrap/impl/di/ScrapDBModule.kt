package org.bmsk.lifemash.core.repo.scrap.impl.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.repo.scrap.impl.dao.ScrapNewsDao
import org.bmsk.lifemash.core.repo.scrap.impl.db.ScrapNewsDB
import org.bmsk.lifemash.core.repo.scrap.impl.entity.MIGRATION_1_2
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ScrapDBModule {
    @Provides
    fun provideScrapNewsDB(
        db: ScrapNewsDB
    ): ScrapNewsDao = db.newsDao()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ScrapNewsDB = Room.databaseBuilder(
        context = context,
        klass = ScrapNewsDB::class.java,
        name = ScrapNewsDB.SCRAP_NEWS_DB_NAME
    ).addMigrations(MIGRATION_1_2).build()
}