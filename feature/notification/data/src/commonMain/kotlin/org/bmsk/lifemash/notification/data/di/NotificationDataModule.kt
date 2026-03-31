package org.bmsk.lifemash.notification.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.notification.data.api.NotificationApi
import org.bmsk.lifemash.notification.data.repository.NotificationRepositoryImpl
import org.bmsk.lifemash.notification.domain.repository.NotificationRepository
import org.koin.dsl.module

val notificationDataModule = module {
    single { NotificationApi(get<HttpClient>()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
