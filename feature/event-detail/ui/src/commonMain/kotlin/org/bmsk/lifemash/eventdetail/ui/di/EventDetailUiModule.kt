package org.bmsk.lifemash.eventdetail.ui.di

import org.bmsk.lifemash.eventdetail.ui.EventDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val eventDetailUiModule = module {
    viewModel { EventDetailViewModel(get()) }
}
