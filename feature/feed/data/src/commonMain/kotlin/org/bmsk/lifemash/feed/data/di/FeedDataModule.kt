package org.bmsk.lifemash.feed.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.feed.data.api.FeedApi
import org.bmsk.lifemash.feed.data.repository.FeedRepositoryImpl
import org.bmsk.lifemash.feed.domain.repository.FeedRepository
import org.koin.dsl.module

val feedDataModule = module {
    single { FeedApi(get<HttpClient>()) }
    single<FeedRepository> { FeedRepositoryImpl(get()) }
}
