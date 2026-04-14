package org.bmsk.lifemash.data.remote.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.auth.dto.AuthTokenResponse
import org.bmsk.lifemash.data.remote.auth.dto.AuthUserResponse
import org.bmsk.lifemash.data.remote.auth.dto.EmailSignInRequest
import org.bmsk.lifemash.data.remote.auth.dto.GoogleSignInRequest
import org.bmsk.lifemash.data.remote.auth.dto.KakaoSignInRequest
import org.bmsk.lifemash.data.remote.auth.dto.RefreshTokenRequest

class AuthApi(private val client: HttpClient) {

    private val base = "/api/v1/auth"

    suspend fun signInWithKakao(accessToken: String): AuthTokenResponse =
        client.post("$base/kakao") {
            contentType(ContentType.Application.Json)
            setBody(KakaoSignInRequest(accessToken))
        }.body()

    suspend fun signInWithGoogle(idToken: String): AuthTokenResponse =
        client.post("$base/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleSignInRequest(idToken))
        }.body()

    suspend fun signInWithEmail(email: String, password: String): AuthTokenResponse =
        client.post("$base/email") {
            contentType(ContentType.Application.Json)
            setBody(EmailSignInRequest(email, password))
        }.body()

    suspend fun refreshToken(refreshToken: String): AuthTokenResponse =
        client.post("$base/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }.body()

    suspend fun signOut(): Unit =
        client.post("$base/signout").body()

    suspend fun getMe(accessToken: String): AuthUserResponse =
        client.get("$base/me") {
            bearerAuth(accessToken)
        }.body()
}
