package org.bmsk.lifemash.profile.impl.di

import org.bmsk.lifemash.domain.usecase.calendar.DeleteEventUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMonthEventsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.calendar.UpdateEventUseCase
import org.bmsk.lifemash.domain.usecase.follow.FollowUserUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowersUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowingUseCase
import org.bmsk.lifemash.domain.usecase.follow.UnfollowUserUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileMomentsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetUserProfileUseCase
import org.bmsk.lifemash.domain.usecase.profile.UpdateProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.UpdateProfileUseCase
import org.bmsk.lifemash.profile.impl.FollowListViewModel
import org.bmsk.lifemash.profile.impl.ProfileEditViewModel
import org.bmsk.lifemash.profile.impl.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileUiModule = module {
    viewModel {
        ProfileViewModel(
            GetUserProfileUseCase(get()),
            GetProfileSettingsUseCase(get()),
            GetProfileMomentsUseCase(get()),
            GetMyGroupsUseCase(get()),
            GetMonthEventsUseCase(get()),
            UpdateEventUseCase(get()),
            DeleteEventUseCase(get()),
            FollowUserUseCase(get()),
            UnfollowUserUseCase(get()),
        )
    }
    viewModel {
        ProfileEditViewModel(
            GetUserProfileUseCase(get()),
            GetProfileSettingsUseCase(get()),
            UpdateProfileUseCase(get()),
            UpdateProfileSettingsUseCase(get()),
            get(),
        )
    }
    viewModel {
        FollowListViewModel(
            GetFollowersUseCase(get()),
            GetFollowingUseCase(get()),
            GetMyGroupsUseCase(get()),
            FollowUserUseCase(get()),
            UnfollowUserUseCase(get()),
        )
    }
}
