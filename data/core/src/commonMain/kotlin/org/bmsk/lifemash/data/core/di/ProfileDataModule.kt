package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.data.local.profile.ProfilePreferences
import org.bmsk.lifemash.data.remote.profile.ProfileApi
import org.bmsk.lifemash.data.core.profile.ProfileRepositoryImpl
import org.bmsk.lifemash.data.core.profile.ProfileSettingsRepositoryImpl
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.ProfileSettingsRepository
import org.koin.dsl.module

val profileDataModule = module {
    single { ProfileApi(get<HttpClient>()) }
    single { ProfilePreferences(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<ProfileSettingsRepository> { ProfileSettingsRepositoryImpl(get()) }
}
