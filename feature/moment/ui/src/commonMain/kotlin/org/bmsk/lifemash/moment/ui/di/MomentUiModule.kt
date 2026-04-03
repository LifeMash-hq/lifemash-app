package org.bmsk.lifemash.moment.ui.di

import org.bmsk.lifemash.moment.domain.usecase.CreateMomentUseCase
import org.bmsk.lifemash.moment.ui.PostMomentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val momentUiModule = module {
    factory { CreateMomentUseCase(get()) }
    viewModel { PostMomentViewModel(get(), get(), get()) }
}
