package org.bmsk.lifemash.assistant.domain.di

import org.bmsk.lifemash.assistant.domain.usecase.*
import org.koin.dsl.module

val assistantDomainModule = module {
    factory<SendMessageUseCase> { SendMessageUseCaseImpl(get()) }
    factory<GetConversationsUseCase> { GetConversationsUseCaseImpl(get()) }
    factory<GetConversationUseCase> { GetConversationUseCaseImpl(get()) }
    factory<DeleteConversationUseCase> { DeleteConversationUseCaseImpl(get()) }
    factory<SaveApiKeyUseCase> { SaveApiKeyUseCaseImpl(get()) }
    factory<RemoveApiKeyUseCase> { RemoveApiKeyUseCaseImpl(get()) }
    factory<GetApiKeyStatusUseCase> { GetApiKeyStatusUseCaseImpl(get()) }
    factory<GetUsageUseCase> { GetUsageUseCaseImpl(get()) }
}
