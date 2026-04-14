package org.bmsk.lifemash.main.di

import io.ktor.client.HttpClient
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
import org.bmsk.lifemash.data.remote.auth.AuthApi
import org.bmsk.lifemash.data.local.auth.TokenStorage
import org.bmsk.lifemash.data.remote.engine.createPlatformHttpClientEngine
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun httpClientModule(backendBaseUrl: String) = module {
    single<HttpClient>(named("auth")) {
        HttpClient(createPlatformHttpClientEngine()) {
            defaultRequest {
                url(backendBaseUrl)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) { level = LogLevel.HEADERS }
        }
    }

    single<HttpClient> {
        val tokenStorage: TokenStorage = get()
        val authApi: AuthApi = get()
        HttpClient(createPlatformHttpClientEngine()) {
            defaultRequest {
                url(backendBaseUrl)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenStorage.getAccessToken() ?: return@loadTokens null
                        val refresh = tokenStorage.getRefreshToken() ?: return@loadTokens null
                        BearerTokens(access, refresh)
                    }
                    refreshTokens {
                        val refresh = tokenStorage.getRefreshToken() ?: return@refreshTokens null
                        try {
                            val newToken = authApi.refreshToken(refresh)
                            tokenStorage.saveTokens(newToken.accessToken, newToken.refreshToken)
                            BearerTokens(newToken.accessToken, newToken.refreshToken)
                        } catch (_: Exception) {
                            tokenStorage.clearTokens()
                            tokenStorage.clearUser()
                            null
                        }
                    }
                }
            }
            install(Logging) { level = LogLevel.HEADERS }
        }
    }

    single<HttpClient>(named("upload")) {
        HttpClient(createPlatformHttpClientEngine()) {
            install(Logging) { level = LogLevel.HEADERS }
        }
    }
}
