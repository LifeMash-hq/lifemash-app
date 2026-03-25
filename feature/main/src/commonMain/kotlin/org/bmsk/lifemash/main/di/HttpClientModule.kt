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
import org.bmsk.lifemash.auth.data.api.AuthApi
import org.bmsk.lifemash.auth.data.api.dto.toDomain
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.bmsk.lifemash.data.network.engine.createPlatformHttpClientEngine
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
            install(Logging) { level = LogLevel.BODY }
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
}
