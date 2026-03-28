package org.bmsk.lifemash.explore.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.explore.data.api.ExploreApi
import org.bmsk.lifemash.explore.data.repository.ExploreRepositoryImpl
import org.bmsk.lifemash.explore.domain.repository.ExploreRepository
import org.koin.dsl.module

val exploreDataModule = module {
    single { ExploreApi(get<HttpClient>()) }
    single<ExploreRepository> { ExploreRepositoryImpl(get()) }
}
