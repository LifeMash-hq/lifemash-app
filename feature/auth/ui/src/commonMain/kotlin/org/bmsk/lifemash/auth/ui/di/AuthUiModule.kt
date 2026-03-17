package org.bmsk.lifemash.auth.ui.di

import org.bmsk.lifemash.auth.ui.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authUiModule = module {
    viewModel { AuthViewModel(get(), get()) }
}
