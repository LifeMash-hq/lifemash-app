package org.bmsk.lifemash.data.remote.moment

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.moment.dto.CreateMomentRequest
import org.bmsk.lifemash.data.remote.moment.dto.MomentResponse

class MomentApi(private val client: HttpClient) {

    private val base = "/api/v1/moments"

    suspend fun create(request: CreateMomentRequest): MomentResponse =
        client.post(base) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUserMoments(userId: String): List<MomentResponse> =
        client.get("$base/user/$userId").body()

    suspend fun delete(momentId: String): Unit =
        client.delete("$base/$momentId").body()
}
