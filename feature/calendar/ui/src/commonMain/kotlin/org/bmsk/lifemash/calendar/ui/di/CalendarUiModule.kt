package org.bmsk.lifemash.calendar.ui.di

import org.bmsk.lifemash.calendar.ui.CalendarViewModel
import org.bmsk.lifemash.calendar.ui.EventCreateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarUiModule = module {
    viewModel { CalendarViewModel(get(), get()) }
    viewModel { EventCreateViewModel(get(), get()) }
}
