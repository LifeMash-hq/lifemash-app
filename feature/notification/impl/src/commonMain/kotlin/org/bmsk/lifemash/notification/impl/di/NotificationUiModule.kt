package org.bmsk.lifemash.notification.impl.di

import org.bmsk.lifemash.domain.usecase.notification.GetNotificationsUseCase
import org.bmsk.lifemash.domain.usecase.notification.MarkNotificationReadUseCase
import org.bmsk.lifemash.notification.impl.NotificationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notificationUiModule = module {
    viewModel {
        NotificationViewModel(
            GetNotificationsUseCase(get()),
            MarkNotificationReadUseCase(get()),
        )
    }
}
