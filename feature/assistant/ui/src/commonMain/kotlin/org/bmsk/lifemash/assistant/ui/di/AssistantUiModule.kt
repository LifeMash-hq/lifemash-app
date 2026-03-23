package org.bmsk.lifemash.assistant.ui.di

import org.bmsk.lifemash.assistant.ui.AssistantViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val assistantUiModule = module {
    viewModel { AssistantViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
