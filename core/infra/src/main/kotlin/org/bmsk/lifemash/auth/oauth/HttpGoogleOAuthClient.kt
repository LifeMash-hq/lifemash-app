package org.bmsk.lifemash.auth.oauth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.bmsk.lifemash.plugins.UnauthorizedException

class HttpGoogleOAuthClient(private val httpClient: HttpClient) : GoogleOAuthClient {

    override suspend fun verifyIdToken(idToken: String): GoogleUser {
        val response = httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
            parameter("id_token", idToken)
        }
        if (response.status != HttpStatusCode.OK) throw UnauthorizedException("Invalid Google token")
        return response.body()
    }
}
