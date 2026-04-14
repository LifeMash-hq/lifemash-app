package org.bmsk.lifemash.eventdetail.impl.di

import org.bmsk.lifemash.domain.usecase.eventdetail.AddEventCommentUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.GetEventDetailUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.ToggleEventJoinUseCase
import org.bmsk.lifemash.eventdetail.impl.EventDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val eventDetailUiModule = module {
    viewModel {
        EventDetailViewModel(
            GetEventDetailUseCase(get()),
            ToggleEventJoinUseCase(get()),
            AddEventCommentUseCase(get()),
        )
    }
}
