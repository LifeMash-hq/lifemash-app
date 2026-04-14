package org.bmsk.lifemash.auth.impl.di

import org.bmsk.lifemash.auth.impl.AuthViewModel
import org.bmsk.lifemash.domain.usecase.auth.GetCurrentUserUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithEmailUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithGoogleUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithKakaoUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authUiModule = module {
    viewModel {
        AuthViewModel(
            SignInWithKakaoUseCase(get()),
            SignInWithGoogleUseCase(get()),
            SignInWithEmailUseCase(get()),
            GetCurrentUserUseCase(get()),
        )
    }
}
