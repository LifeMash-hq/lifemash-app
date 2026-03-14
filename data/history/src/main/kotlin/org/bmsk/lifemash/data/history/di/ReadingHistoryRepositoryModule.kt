package org.bmsk.lifemash.data.history.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.data.history.repository.ReadingHistoryRepositoryImpl
import org.bmsk.lifemash.domain.history.repository.ReadingHistoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ReadingHistoryRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindReadingHistoryRepository(
        impl: ReadingHistoryRepositoryImpl,
    ): ReadingHistoryRepository
}
