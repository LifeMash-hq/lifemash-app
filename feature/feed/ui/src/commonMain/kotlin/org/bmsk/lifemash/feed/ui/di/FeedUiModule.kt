package org.bmsk.lifemash.feed.ui.di

import org.bmsk.lifemash.feed.ui.FeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val feedUiModule = module {
    viewModel { FeedViewModel(get()) }
}
