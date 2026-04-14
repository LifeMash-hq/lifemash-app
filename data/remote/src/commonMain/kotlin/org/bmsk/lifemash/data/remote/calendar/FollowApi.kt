package org.bmsk.lifemash.data.remote.calendar

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.bmsk.lifemash.data.remote.calendar.dto.FollowerDto

class FollowApi(private val client: HttpClient) {

    suspend fun getFollowers(userId: String): List<FollowerDto> =
        client.get("/api/v1/follow/$userId/followers").body()

    suspend fun getFollowing(userId: String): List<FollowerDto> =
        client.get("/api/v1/follow/$userId/following").body()

    suspend fun follow(userId: String): Unit =
        client.post("/api/v1/users/$userId/follow").body()

    suspend fun unfollow(userId: String): Unit =
        client.delete("/api/v1/users/$userId/follow").body()
}
