package org.bmsk.lifemash.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val koinAppModule = module {
    // Auth API용 (로그인, 토큰 갱신 — Auth 플러그인 없음, 무한 루프 방지)
    single<HttpClient>(named("auth")) {
        HttpClient(OkHttp) {
            defaultRequest {
                url(BACKEND_BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    // 일반 API용 (Bearer + refreshTokens)
    single<HttpClient> {
        val tokenStorage: TokenStorage = get()
        val authApi: AuthApi = get()
        HttpClient(OkHttp) {
            defaultRequest {
                url(BACKEND_BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
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
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }
}

private val BACKEND_BASE_URL get() = org.bmsk.lifemash.BuildConfig.BACKEND_BASE_URL
