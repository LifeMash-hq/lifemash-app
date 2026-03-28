package org.bmsk.lifemash.profile.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.profile.data.api.ProfileApi
import org.bmsk.lifemash.profile.data.repository.ProfileRepositoryImpl
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository
import org.koin.dsl.module

val profileDataModule = module {
    single { ProfileApi(get<HttpClient>()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}
