package org.bmsk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.data.repository.NewsRepository
import org.bmsk.data.repository.NewsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun provideNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl,
    ): NewsRepository
}