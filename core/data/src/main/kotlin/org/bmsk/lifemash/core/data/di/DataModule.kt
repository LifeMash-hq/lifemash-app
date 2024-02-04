package org.bmsk.lifemash.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.bmsk.lifemash.core.data.repository.NewsRepositoryImpl
import org.bmsk.lifemash.core.domain.repository.NewsRepository
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
