package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.feed.FeedRepository
import org.bmsk.lifemash.data.remote.feed.FeedApi
import org.bmsk.lifemash.data.core.feed.FeedRepositoryImpl
import org.koin.dsl.module

val feedDataModule = module {
    single { FeedApi(get<HttpClient>()) }
    single<FeedRepository> { FeedRepositoryImpl(get()) }
}
