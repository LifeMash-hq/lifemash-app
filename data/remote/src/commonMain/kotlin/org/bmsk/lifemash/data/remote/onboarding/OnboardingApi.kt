package org.bmsk.lifemash.data.remote.onboarding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.onboarding.dto.CheckHandleResponse
import org.bmsk.lifemash.data.remote.onboarding.dto.UpdateProfileRequest

class OnboardingApi(private val client: HttpClient) {

    suspend fun checkHandle(handle: String): CheckHandleResponse =
        client.get("/api/v1/profile/check-handle") {
            parameter("id", handle)
        }.body()

    suspend fun updateProfile(body: UpdateProfileRequest) {
        client.patch("/api/v1/users/me/profile") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}
