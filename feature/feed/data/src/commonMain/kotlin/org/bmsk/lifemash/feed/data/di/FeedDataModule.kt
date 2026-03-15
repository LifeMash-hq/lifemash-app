package org.bmsk.lifemash.feed.data.di

import androidx.room.RoomDatabase
import org.bmsk.lifemash.feed.data.history.db.ReadingHistoryDB
import org.bmsk.lifemash.feed.data.history.repository.ReadingHistoryRepositoryImpl
import org.bmsk.lifemash.feed.data.repository.ArticleRepositoryImpl
import org.bmsk.lifemash.feed.data.subscription.datastore.CategorySubscriptionDataSource
import org.bmsk.lifemash.feed.data.subscription.datastore.CategorySubscriptionDataSourceImpl
import org.bmsk.lifemash.feed.data.subscription.repository.CategorySubscriptionRepositoryImpl
import org.bmsk.lifemash.feed.domain.history.repository.ReadingHistoryRepository
import org.bmsk.lifemash.feed.domain.repository.ArticleRepository
import org.bmsk.lifemash.feed.domain.subscription.repository.CategorySubscriptionRepository
import org.koin.dsl.module

fun feedDataModule(dbBuilder: RoomDatabase.Builder<ReadingHistoryDB>) = module {
    single<ReadingHistoryDB> { dbBuilder.build() }
    single { get<ReadingHistoryDB>().readingHistoryDao() }
    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
    single<ReadingHistoryRepository> { ReadingHistoryRepositoryImpl(get()) }
    single<CategorySubscriptionDataSource> { CategorySubscriptionDataSourceImpl(get()) }
    single<CategorySubscriptionRepository> { CategorySubscriptionRepositoryImpl(get()) }
}
