package org.bmsk.lifemash.data.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.data.remote.auth.AuthApi
import org.bmsk.lifemash.data.core.auth.AuthRepositoryImpl
import org.bmsk.lifemash.data.local.auth.TokenStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun authDataModule(dataStore: DataStore<Preferences>) = module {
    single { TokenStorage(dataStore) }
    single { AuthApi(get<HttpClient>(named("auth"))) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}
