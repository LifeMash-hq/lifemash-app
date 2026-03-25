package org.bmsk.lifemash.auth.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.model.auth.AuthTokenDto
import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.model.auth.GoogleSignInRequest
import org.bmsk.lifemash.model.auth.KakaoSignInRequest
import org.bmsk.lifemash.model.auth.RefreshTokenRequest

class AuthApi(private val client: HttpClient) {

    private val base = "/api/v1/auth"

    suspend fun signInWithKakao(accessToken: String): AuthTokenDto =
        client.post("$base/kakao") {
            contentType(ContentType.Application.Json)
            setBody(KakaoSignInRequest(accessToken))
        }.body()

    suspend fun signInWithGoogle(idToken: String): AuthTokenDto =
        client.post("$base/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleSignInRequest(idToken))
        }.body()

    suspend fun refreshToken(refreshToken: String): AuthTokenDto =
        client.post("$base/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }.body()

    suspend fun signOut(): Unit =
        client.post("$base/signout").body()

    suspend fun getMe(accessToken: String): AuthUserDto =
        client.get("$base/me") {
            bearerAuth(accessToken)
        }.body()
}
