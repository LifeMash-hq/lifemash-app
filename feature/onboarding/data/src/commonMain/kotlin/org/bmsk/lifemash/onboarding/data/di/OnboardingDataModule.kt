package org.bmsk.lifemash.onboarding.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.onboarding.data.api.OnboardingApi
import org.bmsk.lifemash.onboarding.data.repository.OnboardingRepositoryImpl
import org.bmsk.lifemash.onboarding.domain.repository.OnboardingRepository
import org.koin.dsl.module

val onboardingDataModule = module {
    single { OnboardingApi(get<HttpClient>()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
}
