package org.bmsk.lifemash.onboarding.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.onboarding.data.api.dto.CheckHandleResponse
import org.bmsk.lifemash.onboarding.data.api.dto.UpdateProfileBody

internal class OnboardingApi(private val client: HttpClient) {

    suspend fun checkHandle(handle: String): CheckHandleResponse =
        client.get("/api/v1/profile/check-handle") {
            parameter("id", handle)
        }.body()

    suspend fun updateProfile(body: UpdateProfileBody) {
        client.patch("/api/v1/users/me/profile") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}
