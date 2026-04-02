package org.bmsk.lifemash.moment.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MomentDto

internal class MomentApi(private val client: HttpClient) {

    private val base = "/api/v1/moments"

    suspend fun create(request: CreateMomentRequest): MomentDto =
        client.post(base) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUserMoments(userId: String): List<MomentDto> =
        client.get("$base/user/$userId").body()

    suspend fun delete(momentId: String): Unit =
        client.delete("$base/$momentId").body()
}
