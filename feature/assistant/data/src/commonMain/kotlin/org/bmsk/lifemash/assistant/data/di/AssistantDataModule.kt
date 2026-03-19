package org.bmsk.lifemash.assistant.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.assistant.data.api.AssistantApi
import org.bmsk.lifemash.assistant.data.api.SseClient
import org.bmsk.lifemash.assistant.data.repository.ApiKeyRepositoryImpl
import org.bmsk.lifemash.assistant.data.repository.AssistantRepositoryImpl
import org.bmsk.lifemash.assistant.domain.repository.ApiKeyRepository
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository
import org.koin.dsl.module

val assistantDataModule = module {
    single { AssistantApi(get<HttpClient>()) }
    single { SseClient(get<HttpClient>()) }
    single<AssistantRepository> { AssistantRepositoryImpl(get(), get()) }
    single<ApiKeyRepository> { ApiKeyRepositoryImpl(get()) }
}
