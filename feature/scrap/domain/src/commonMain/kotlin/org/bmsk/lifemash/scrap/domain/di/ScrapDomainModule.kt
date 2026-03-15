package org.bmsk.lifemash.scrap.domain.di

import org.bmsk.lifemash.scrap.domain.usecase.AddScrapUseCase
import org.bmsk.lifemash.scrap.domain.usecase.AddScrapUseCaseImpl
import org.bmsk.lifemash.scrap.domain.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.scrap.domain.usecase.DeleteScrappedArticleUseCaseImpl
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticleIdsUseCase
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticleIdsUseCaseImpl
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticlesUseCase
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticlesUseCaseImpl
import org.koin.dsl.module

val scrapDomainModule = module {
    factory<AddScrapUseCase> { AddScrapUseCaseImpl(get()) }
    factory<DeleteScrappedArticleUseCase> { DeleteScrappedArticleUseCaseImpl(get()) }
    factory<GetScrappedArticlesUseCase> { GetScrappedArticlesUseCaseImpl(get()) }
    factory<GetScrappedArticleIdsUseCase> { GetScrappedArticleIdsUseCaseImpl(get()) }
}
