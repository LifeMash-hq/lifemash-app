package org.bmsk.lifemash.core.repo.search.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.repo.search.api.NewsRepository
import org.bmsk.lifemash.core.repo.search.impl.NewsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    @Singleton
    fun provideNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl,
    ): NewsRepository
}
