package org.bmsk.lifemash.feed.domain.di

import org.bmsk.lifemash.feed.domain.usecase.GetFeedUseCase
import org.bmsk.lifemash.feed.domain.usecase.GetFeedUseCaseImpl
import org.koin.dsl.module

val feedDomainModule = module {
    factory<GetFeedUseCase> { GetFeedUseCaseImpl(get()) }
}
