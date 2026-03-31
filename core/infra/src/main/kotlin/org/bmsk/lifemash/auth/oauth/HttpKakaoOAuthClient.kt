package org.bmsk.lifemash.auth.oauth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.bmsk.lifemash.plugins.UnauthorizedException

class HttpKakaoOAuthClient(private val httpClient: HttpClient) : KakaoOAuthClient {

    override suspend fun getUser(accessToken: String): KakaoUser {
        val response = httpClient.get("https://kapi.kakao.com/v2/user/me") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        if (response.status != HttpStatusCode.OK) throw UnauthorizedException("Invalid Kakao token")
        return response.body()
    }
}
