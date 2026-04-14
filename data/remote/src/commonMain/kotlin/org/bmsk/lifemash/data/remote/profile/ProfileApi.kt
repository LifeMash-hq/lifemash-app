package org.bmsk.lifemash.data.remote.profile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.profile.dto.MomentDto
import org.bmsk.lifemash.data.remote.profile.dto.ProfileDto
import org.bmsk.lifemash.data.remote.profile.dto.UpdateProfileRequest

class ProfileApi(private val client: HttpClient) {
    suspend fun getProfile(userId: String): ProfileDto =
        client.get("/api/v1/users/$userId/profile").body()

    suspend fun updateProfile(body: UpdateProfileRequest): ProfileDto =
        client.patch("/api/v1/users/me/profile") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun getMoments(userId: String): List<MomentDto> =
        client.get("/api/v1/users/$userId/moments").body()
}
