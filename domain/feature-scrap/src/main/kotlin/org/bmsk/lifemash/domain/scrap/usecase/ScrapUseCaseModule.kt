package org.bmsk.lifemash.domain.scrap.usecase

import dagger.Module
import dagger.Provides
import org.bmsk.lifemash.domain.scrap.repository.ScrapRepository

@Module
object ScrapUseCaseModule {

    @Provides
    fun provideAddScrapUseCase(
        repo: ScrapRepository
    ): AddScrapUseCase = AddScrapUseCaseImpl(repo)

    @Provides
    fun provideDeleteScrappedArticleUseCase(
        repo: ScrapRepository
    ): DeleteScrappedArticleUseCase = DeleteScrappedArticleUseCaseImpl(repo)

    @Provides
    fun provideGetScrappedArticlesUseCase(
        repo: ScrapRepository
    ): GetScrappedArticlesUseCase = GetScrappedArticlesUseCaseImpl(repo)
}