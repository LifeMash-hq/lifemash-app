package org.bmsk.lifemash.notification.domain.di

import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository
import org.bmsk.lifemash.notification.domain.usecase.AddKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.GetKeywordsUseCase
import org.bmsk.lifemash.notification.domain.usecase.RemoveKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.SyncFcmTokenUseCase
import org.koin.dsl.module

val notificationDomainModule = module {
    factory { GetKeywordsUseCase(get<NotificationKeywordRepository>()) }
    factory { AddKeywordUseCase(get<NotificationKeywordRepository>()) }
    factory { RemoveKeywordUseCase(get<NotificationKeywordRepository>()) }
    factory { SyncFcmTokenUseCase(get<NotificationKeywordRepository>()) }
}
