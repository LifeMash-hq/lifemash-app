package org.bmsk.lifemash.domain.feed.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeedUseCaseModule {

    @Binds
    abstract fun provideGetArticlesUseCase(
        impl: GetArticlesUseCaseImpl
    ): GetArticlesUseCase

    @Binds
    abstract fun provideSearchArticlesUseCase(
        impl: SearchArticlesUseCaseImpl
    ): SearchArticlesUseCase
}