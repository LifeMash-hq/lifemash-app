package org.bmsk.lifemash.onboarding.ui.di

import org.bmsk.lifemash.onboarding.ui.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingUiModule = module {
    viewModel { OnboardingViewModel(get()) }
}
