package org.bmsk.lifemash.data.network.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.data.network.service.GoogleNewsService
import org.bmsk.lifemash.data.network.service.LifeMashFirebaseService
import org.bmsk.lifemash.data.network.service.LifeMashFirebaseServiceImpl
import org.bmsk.lifemash.data.network.service.SbsNewsService
import org.bmsk.lifemash.data.network.service.SearchService
import org.koin.dsl.module

val iosNetworkKoinModule = module {
    single { GoogleNewsService(get<HttpClient>()) }
    single { SbsNewsService(get<HttpClient>()) }
    single { SearchService(get<HttpClient>()) }
    single<LifeMashFirebaseService> { LifeMashFirebaseServiceImpl(get()) }
}
