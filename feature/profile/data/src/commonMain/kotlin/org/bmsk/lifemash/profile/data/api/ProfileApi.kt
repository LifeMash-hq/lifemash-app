package org.bmsk.lifemash.profile.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.profile.data.api.dto.*

internal class ProfileApi(private val client: HttpClient) {
    suspend fun getProfile(userId: String): ProfileDto =
        client.get("/api/v1/users/$userId/profile").body()

    suspend fun updateProfile(body: UpdateProfileBody): ProfileDto =
        client.patch("/api/v1/users/me/profile") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun follow(userId: String): Unit =
        client.post("/api/v1/users/$userId/follow").body()

    suspend fun unfollow(userId: String): Unit =
        client.delete("/api/v1/users/$userId/follow").body()

    suspend fun getMoments(userId: String): List<MomentDto> =
        client.get("/api/v1/users/$userId/moments").body()
}
