package org.bmsk.lifemash.calendar.impl.di

import org.bmsk.lifemash.calendar.impl.CalendarViewModel
import org.bmsk.lifemash.calendar.impl.EventCreateViewModel
import org.bmsk.lifemash.calendar.impl.VisibilitySheetViewModel
import org.bmsk.lifemash.domain.usecase.auth.GetCurrentUserUseCase
import org.bmsk.lifemash.domain.usecase.calendar.CreateEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.CreateGroupUseCase
import org.bmsk.lifemash.domain.usecase.calendar.DeleteEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMonthEventsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.JoinGroupUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateGroupNameUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowersUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarUiModule = module {
    viewModel {
        CalendarViewModel(
            GetMonthEventsUseCase(get()),
            GetMyGroupsUseCase(get()),
            CreateGroupUseCase(get()),
            JoinGroupUseCase(get()),
            UpdateGroupNameUseCase(get()),
            DeleteEventUseCase(get()),
        )
    }
    viewModel {
        EventCreateViewModel(
            GetMyGroupsUseCase(get()),
            CreateEventUseCase(get()),
            UpdateEventUseCase(get()),
        )
    }
    viewModel {
        VisibilitySheetViewModel(
            GetCurrentUserUseCase(get()),
            GetMyGroupsUseCase(get()),
            GetFollowersUseCase(get()),
            CreateGroupUseCase(get()),
        )
    }
}
