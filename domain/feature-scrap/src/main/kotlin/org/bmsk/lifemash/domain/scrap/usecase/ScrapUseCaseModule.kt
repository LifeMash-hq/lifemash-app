package org.bmsk.lifemash.domain.scrap.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ScrapUseCaseModule {

    @Binds
    abstract fun bindAddScrapUseCase(
        impl: AddScrapUseCaseImpl
    ): AddScrapUseCase

    @Binds
    abstract fun bindDeleteScrappedArticleUseCase(
        impl: DeleteScrappedArticleUseCaseImpl
    ): DeleteScrappedArticleUseCase

    @Binds
    abstract fun bindGetScrappedArticlesUseCase(
        impl: GetScrappedArticlesUseCaseImpl
    ): GetScrappedArticlesUseCase
}