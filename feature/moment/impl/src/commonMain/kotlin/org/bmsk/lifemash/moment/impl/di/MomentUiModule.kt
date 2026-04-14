package org.bmsk.lifemash.moment.impl.di

import org.bmsk.lifemash.domain.usecase.moment.CreateMomentUseCase
import org.bmsk.lifemash.domain.usecase.moment.GetCurrentMonthGroupEventsUseCase
import org.bmsk.lifemash.moment.impl.PostMomentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val momentUiModule = module {
    viewModel {
        PostMomentViewModel(
            CreateMomentUseCase(get()),
            GetCurrentMonthGroupEventsUseCase(get(), get()),
        )
    }
}
