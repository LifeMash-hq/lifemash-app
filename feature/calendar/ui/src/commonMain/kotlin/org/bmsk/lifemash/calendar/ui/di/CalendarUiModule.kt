package org.bmsk.lifemash.calendar.ui.di

import org.bmsk.lifemash.calendar.ui.CalendarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarUiModule = module {
    viewModel { CalendarViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
