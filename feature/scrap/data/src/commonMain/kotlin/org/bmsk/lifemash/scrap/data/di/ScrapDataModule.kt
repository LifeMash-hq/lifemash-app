package org.bmsk.lifemash.scrap.data.di

import androidx.room.RoomDatabase
import org.bmsk.lifemash.scrap.data.db.ScrapArticleDB
import org.bmsk.lifemash.scrap.data.repository.ScrapRepositoryImpl
import org.bmsk.lifemash.scrap.domain.repository.ScrapRepository
import org.koin.dsl.module

fun scrapDataModule(dbBuilder: RoomDatabase.Builder<ScrapArticleDB>) = module {
    single<ScrapArticleDB> { dbBuilder.build() }
    single { get<ScrapArticleDB>().articleDao() }
    single<ScrapRepository> { ScrapRepositoryImpl(get()) }
}
