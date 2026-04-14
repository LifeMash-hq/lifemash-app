package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.onboarding.OnboardingRepository
import org.bmsk.lifemash.data.remote.onboarding.OnboardingApi
import org.bmsk.lifemash.data.core.onboarding.OnboardingRepositoryImpl
import org.koin.dsl.module

val onboardingDataModule = module {
    single { OnboardingApi(get<HttpClient>()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
}
