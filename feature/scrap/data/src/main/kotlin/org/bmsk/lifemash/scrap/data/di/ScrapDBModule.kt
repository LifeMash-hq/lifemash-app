package org.bmsk.lifemash.scrap.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.scrap.data.dao.ScrapArticleDao
import org.bmsk.lifemash.scrap.data.db.ScrapArticleDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ScrapDBModule {
    @Provides
    fun provideScrapArticleDao(
        db: ScrapArticleDB
    ): ScrapArticleDao = db.articleDao()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ScrapArticleDB = Room.databaseBuilder(
        context = context,
        klass = ScrapArticleDB::class.java,
        name = "scrap_article_db"
    ).build()
}
