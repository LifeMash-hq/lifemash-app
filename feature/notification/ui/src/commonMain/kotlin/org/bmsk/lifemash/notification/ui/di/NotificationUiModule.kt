package org.bmsk.lifemash.notification.ui.di

import org.bmsk.lifemash.notification.ui.NotificationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notificationUiModule = module {
    viewModel { NotificationViewModel(get()) }
}
