package org.bmsk.lifemash.history.ui.di

import org.bmsk.lifemash.history.ui.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val historyUiModule = module {
    viewModel { HistoryViewModel(get()) }
}
