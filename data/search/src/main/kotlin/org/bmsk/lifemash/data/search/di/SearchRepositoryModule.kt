package org.bmsk.lifemash.data.search.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.data.search.NewsRepositoryImpl
import org.bmsk.lifemash.domain.search.repository.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SearchRepositoryModule { // Renamed from DataModule

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        // Renamed from provideNewsRepository
        impl: NewsRepositoryImpl,
    ): NewsRepository
}