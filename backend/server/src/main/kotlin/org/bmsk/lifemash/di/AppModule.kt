package org.bmsk.lifemash.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.assistant.*
import org.bmsk.lifemash.assistant.AssistantServiceImpl
import org.bmsk.lifemash.assistant.tools.CalendarTool
import org.bmsk.lifemash.marketplace.ToolManifestFetcher
import org.bmsk.lifemash.auth.AuthService
import org.bmsk.lifemash.auth.AuthServiceImpl
import org.bmsk.lifemash.blocks.BlocksService
import org.bmsk.lifemash.blocks.BlocksServiceImpl
import org.bmsk.lifemash.marketplace.ExposedMarketplaceRepository
import org.bmsk.lifemash.marketplace.MarketplaceRepository
import org.bmsk.lifemash.marketplace.MarketplaceService
import org.bmsk.lifemash.marketplace.MarketplaceServiceImpl
import org.bmsk.lifemash.auth.oauth.*
import org.bmsk.lifemash.comment.CommentRepository
import org.bmsk.lifemash.comment.CommentService
import org.bmsk.lifemash.comment.CommentServiceImpl
import org.bmsk.lifemash.comment.ExposedCommentRepository
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.event.EventServiceImpl
import org.bmsk.lifemash.event.ExposedEventRepository
import org.bmsk.lifemash.group.ExposedGroupRepository
import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.group.GroupService
import org.bmsk.lifemash.group.GroupServiceImpl
import org.bmsk.lifemash.notification.FcmService
import org.bmsk.lifemash.notification.FirebaseFcmService
import org.bmsk.lifemash.user.ExposedUserRepository
import org.bmsk.lifemash.user.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module

private val productionModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    single<UserRepository> { ExposedUserRepository() }
    single<GroupRepository> { ExposedGroupRepository() }
    single<EventRepository> { ExposedEventRepository() }
    single<CommentRepository> { ExposedCommentRepository() }

    single<KakaoOAuthClient> { HttpKakaoOAuthClient(get()) }
    single<GoogleOAuthClient> { HttpGoogleOAuthClient(get()) }
    single<FcmService> { FirebaseFcmService(get()) }

    single<AuthService> { AuthServiceImpl(get(), get(), get()) }
    single<GroupService> { GroupServiceImpl(get()) }
    single<EventService> { EventServiceImpl(get(), get(), get()) }
    single<CommentService> { CommentServiceImpl(get(), get(), get()) }

    single<AssistantRepository> { ExposedAssistantRepository() }
    single<AssistantUsageRepository> { ExposedAssistantUsageRepository() }
    single<UserApiKeyRepository> { ExposedUserApiKeyRepository() }
    single<ClaudeApiClient> { HttpClaudeApiClient(get()) }
    single { CalendarTool(get(), get(), get()) }
    single { ToolRegistry(get()) }
    single { ExternalToolExecutor(get()) }
    single<AssistantService> { AssistantServiceImpl(get(), get(), get(), get(), get(), get(), get()) }
    single<BlocksService> { BlocksServiceImpl(get(), get()) }

    single<MarketplaceRepository> { ExposedMarketplaceRepository() }
    single { ToolManifestFetcher(get()) }
    single<MarketplaceService> { MarketplaceServiceImpl(get(), get()) }
}

/** CoreModule → ProductionModule → StubModule 순으로 classpath 탐색 */
fun resolveBackendModule(): Module {
    runCatching {
        val clazz = Class.forName("org.bmsk.lifemash.core.di.CoreModuleKt")
        val module = clazz.getMethod("getCoreModule").invoke(null) as Module
        println("✅ CoreModule loaded (lifemash-core)")
        return module
    }

    runCatching {
        Class.forName("org.bmsk.lifemash.user.ExposedUserRepository")
        println("✅ ProductionModule loaded (backend:server)")
        return productionModule
    }

    println("StubModule loaded")
    return stubModule
}
