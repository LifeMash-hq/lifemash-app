package org.bmsk.lifemash.profile.ui.di

import org.bmsk.lifemash.profile.ui.PostMomentViewModel
import org.bmsk.lifemash.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileUiModule = module {
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { PostMomentViewModel(get()) }
}
