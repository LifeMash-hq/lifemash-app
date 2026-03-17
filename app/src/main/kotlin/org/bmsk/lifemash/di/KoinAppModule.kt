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
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.koin.dsl.module

val koinAppModule = module {
    single<HttpClient> {
        val tokenStorage: TokenStorage = get()
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
                }
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }
}

// TODO: Render 배포 후 실제 URL로 교체
private const val BACKEND_BASE_URL = "https://lifemash-backend.onrender.com"
