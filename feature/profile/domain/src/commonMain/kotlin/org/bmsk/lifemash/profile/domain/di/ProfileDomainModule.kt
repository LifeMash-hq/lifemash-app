package org.bmsk.lifemash.profile.domain.di

import org.bmsk.lifemash.profile.domain.usecase.*
import org.koin.dsl.module

val profileDomainModule = module {
    factory<GetProfileUseCase> { GetProfileUseCaseImpl(get()) }
    factory<FollowUseCase> { FollowUseCaseImpl(get()) }
    factory<GetMomentsUseCase> { GetMomentsUseCaseImpl(get()) }
}
