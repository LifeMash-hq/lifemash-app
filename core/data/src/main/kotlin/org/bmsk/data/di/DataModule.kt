package org.bmsk.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.data.repository.NewsRepositoryImpl
import org.bmsk.domain.repository.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun provideNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl,
    ): NewsRepository
}
