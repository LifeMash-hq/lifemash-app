package org.bmsk.lifemash.auth.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.auth.data.api.AuthApi
import org.bmsk.lifemash.auth.data.repository.AuthRepositoryImpl
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import org.koin.dsl.module

val authDataModule = module {
    single { TokenStorage() }
    single { AuthApi(get<HttpClient>()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}
