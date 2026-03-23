package org.bmsk.lifemash.main

import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.bmsk.lifemash.auth.data.api.AuthApi
import org.bmsk.lifemash.auth.data.di.authDataModule
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.bmsk.lifemash.auth.domain.di.authDomainModule
import org.bmsk.lifemash.auth.ui.di.authUiModule
import org.bmsk.lifemash.assistant.data.di.assistantDataModule
import org.bmsk.lifemash.assistant.domain.di.assistantDomainModule
import org.bmsk.lifemash.assistant.ui.di.assistantUiModule
import org.bmsk.lifemash.calendar.data.di.calendarDataModule
import org.bmsk.lifemash.calendar.domain.di.calendarDomainModule
import org.bmsk.lifemash.calendar.ui.di.calendarUiModule
import org.bmsk.lifemash.data.network.di.iosNetworkKoinModule
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.home.data.di.homeDataModule
import org.bmsk.lifemash.home.ui.di.homeDomainModule
import org.bmsk.lifemash.home.ui.di.homeUiModule
import org.bmsk.lifemash.notification.data.db.getNotificationKeywordDBBuilder
import org.bmsk.lifemash.notification.data.di.notificationDataModule
import org.bmsk.lifemash.notification.domain.di.notificationDomainModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

private const val BACKEND_BASE_URL = "https://lifemash-backend.onrender.com"

private val iosDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath {
    (NSHomeDirectory() + "/Documents/lifemash_prefs.preferences_pb").toPath()
}

private val iosAppModule = module {
    // Auth API용 (로그인, 토큰 갱신 — Auth 플러그인 없음, 무한 루프 방지)
    single<HttpClient>(named("auth")) {
        HttpClient(Darwin) {
            engine { configureRequest { setTimeoutInterval(90.0) } }
            defaultRequest {
                url(BACKEND_BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }

    // 일반 API용 (Bearer + refreshTokens)
    single<HttpClient> {
        val tokenStorage: TokenStorage = get()
        val authApi: AuthApi = get()
        HttpClient(Darwin) {
            engine { configureRequest { setTimeoutInterval(90.0) } }
            defaultRequest {
                url(BACKEND_BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenStorage.get()?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }
                    refreshTokens {
                        val stored = tokenStorage.get() ?: return@refreshTokens null
                        try {
                            val newToken = authApi.refreshToken(stored.refreshToken).toDomain()
                            tokenStorage.save(newToken)
                            BearerTokens(newToken.accessToken, newToken.refreshToken)
                        } catch (_: Exception) {
                            tokenStorage.clear()
                            tokenStorage.clearUser()
                            null
                        }
                    }
                }
            }
            install(Logging) { level = LogLevel.BODY }
        }
    }

    single<DataStore<Preferences>> { iosDataStore }
}

fun initKoin() {
    startKoin {
        modules(
            iosAppModule,
            iosNetworkKoinModule,
            authDomainModule,
            authDataModule(iosDataStore),
            calendarDomainModule,
            calendarDataModule,
            notificationDomainModule,
            notificationDataModule(getNotificationKeywordDBBuilder()),
            authUiModule,
            calendarUiModule,
            assistantDomainModule,
            assistantDataModule,
            assistantUiModule,
            homeDomainModule,
            homeDataModule,
            homeUiModule,
            module { viewModel { MainViewModel(get()) } },
        )
    }
}

fun MainViewController() = ComposeUIViewController {
    LifeMashTheme {
        MainScreen()
    }
}
