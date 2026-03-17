package org.bmsk.lifemash.notification.domain.di

import org.bmsk.lifemash.notification.domain.repository.KeywordRepository
import org.bmsk.lifemash.notification.domain.repository.KeywordSyncRepository
import org.bmsk.lifemash.notification.domain.usecase.AddKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.GetKeywordsUseCase
import org.bmsk.lifemash.notification.domain.usecase.RemoveKeywordUseCase
import org.bmsk.lifemash.notification.domain.usecase.SyncKeywordsUseCase
import org.koin.dsl.module

val notificationDomainModule = module {
    factory { GetKeywordsUseCase(get<KeywordRepository>()) }
    factory { AddKeywordUseCase(get<KeywordRepository>()) }
    factory { RemoveKeywordUseCase(get<KeywordRepository>()) }
    factory { SyncKeywordsUseCase(get<KeywordSyncRepository>()) }
}
