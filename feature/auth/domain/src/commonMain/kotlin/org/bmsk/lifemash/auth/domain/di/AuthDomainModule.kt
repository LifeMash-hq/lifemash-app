package org.bmsk.lifemash.auth.domain.di

import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import org.bmsk.lifemash.auth.domain.usecase.GetCurrentUserUseCase
import org.bmsk.lifemash.auth.domain.usecase.SignInWithGoogleUseCase
import org.bmsk.lifemash.auth.domain.usecase.SignInWithKakaoUseCase
import org.bmsk.lifemash.auth.domain.usecase.SignOutUseCase
import org.koin.dsl.module

val authDomainModule = module {
    factory { GetCurrentUserUseCase(get<AuthRepository>()) }
    factory { SignInWithKakaoUseCase(get<AuthRepository>()) }
    factory { SignInWithGoogleUseCase(get<AuthRepository>()) }
    factory { SignOutUseCase(get<AuthRepository>()) }
}
