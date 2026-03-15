package org.bmsk.lifemash.feed.domain.di

import org.bmsk.lifemash.feed.domain.history.usecase.AddToHistoryUseCase
import org.bmsk.lifemash.feed.domain.history.usecase.GetReadArticleIdsUseCase
import org.bmsk.lifemash.feed.domain.history.usecase.GetReadingHistoryUseCase
import org.bmsk.lifemash.feed.domain.history.usecase.GetReadingHistoryUseCaseImpl
import org.bmsk.lifemash.feed.domain.subscription.usecase.GetSubscribedCategoriesUseCase
import org.bmsk.lifemash.feed.domain.subscription.usecase.GetSubscribedCategoriesUseCaseImpl
import org.bmsk.lifemash.feed.domain.subscription.usecase.SetSubscribedCategoriesUseCase
import org.bmsk.lifemash.feed.domain.subscription.usecase.SetSubscribedCategoriesUseCaseImpl
import org.bmsk.lifemash.feed.domain.usecase.GetArticlesUseCase
import org.bmsk.lifemash.feed.domain.usecase.GetArticlesUseCaseImpl
import org.bmsk.lifemash.feed.domain.usecase.SearchArticlesUseCase
import org.bmsk.lifemash.feed.domain.usecase.SearchArticlesUseCaseImpl
import org.koin.dsl.module

val feedDomainModule = module {
    factory<GetArticlesUseCase> { GetArticlesUseCaseImpl(get()) }
    factory<SearchArticlesUseCase> { SearchArticlesUseCaseImpl(get()) }
    factory { AddToHistoryUseCase(get()) }
    factory { GetReadArticleIdsUseCase(get()) }
    factory<GetReadingHistoryUseCase> { GetReadingHistoryUseCaseImpl(get()) }
    factory<GetSubscribedCategoriesUseCase> { GetSubscribedCategoriesUseCaseImpl(get()) }
    factory<SetSubscribedCategoriesUseCase> { SetSubscribedCategoriesUseCaseImpl(get()) }
}
