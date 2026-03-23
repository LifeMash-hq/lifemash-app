package org.bmsk.lifemash.home.ui.di

import org.bmsk.lifemash.home.ui.HomeViewModel
import org.bmsk.lifemash.home.ui.MarketplaceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeUiModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { MarketplaceViewModel(get(), get(), get()) }
}
