package org.bmsk.lifemash.onboarding.impl.di

import org.bmsk.lifemash.domain.usecase.onboarding.CheckHandleUseCase
import org.bmsk.lifemash.domain.usecase.onboarding.SaveOnboardingProfileUseCase
import org.bmsk.lifemash.onboarding.impl.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingUiModule = module {
    viewModel {
        OnboardingViewModel(
            CheckHandleUseCase(get()),
            SaveOnboardingProfileUseCase(get()),
        )
    }
}
