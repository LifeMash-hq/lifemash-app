package org.bmsk.lifemash.scrap.ui.di

import org.bmsk.lifemash.scrap.ui.ScrapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scrapUiModule = module {
    viewModel { ScrapViewModel(get(), get()) }
}
