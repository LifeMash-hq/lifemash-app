package org.bmsk.lifemash.explore.ui.di

import org.bmsk.lifemash.explore.ui.ExploreViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val exploreUiModule = module {
    viewModel { ExploreViewModel(get()) }
}
