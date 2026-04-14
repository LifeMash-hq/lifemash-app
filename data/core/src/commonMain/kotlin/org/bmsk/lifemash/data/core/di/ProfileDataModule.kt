package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.data.remote.profile.ProfileApi
import org.bmsk.lifemash.data.core.profile.ProfileRepositoryImpl
import org.koin.dsl.module

val profileDataModule = module {
    single { ProfileApi(get<HttpClient>()) }
    single { ProfileRepositoryImpl(get()) }
    single<ProfileRepository> { get<ProfileRepositoryImpl>() }
}
