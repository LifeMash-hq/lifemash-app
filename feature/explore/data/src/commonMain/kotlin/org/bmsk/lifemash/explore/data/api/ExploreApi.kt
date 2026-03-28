package org.bmsk.lifemash.explore.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.bmsk.lifemash.explore.domain.model.EventSummary
import org.bmsk.lifemash.explore.domain.model.UserSummary

internal class ExploreApi(private val client: HttpClient) {
    suspend fun searchUsers(query: String): List<UserSummary> =
        client.get("/api/v1/search/users") { url.parameters.append("q", query) }.body()
    suspend fun searchEvents(query: String): List<EventSummary> =
        client.get("/api/v1/search/events") { url.parameters.append("q", query) }.body()
    suspend fun getTrending(): List<UserSummary> =
        client.get("/api/v1/explore/trending").body()
}
