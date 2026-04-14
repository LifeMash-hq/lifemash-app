package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.notification.NotificationRepository
import org.bmsk.lifemash.data.remote.notification.NotificationApi
import org.bmsk.lifemash.data.core.notification.NotificationRepositoryImpl
import org.koin.dsl.module

val notificationDataModule = module {
    single { NotificationApi(get<HttpClient>()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
