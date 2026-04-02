package org.bmsk.lifemash.moment.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.model.upload.PresignedUrlRequest
import org.bmsk.lifemash.model.upload.PresignedUrlResponse

internal class UploadApi(private val client: HttpClient) {

    suspend fun getPresignedUrl(fileName: String, contentType: String): PresignedUrlResponse =
        client.post("/api/v1/upload/presigned-url") {
            this.contentType(ContentType.Application.Json)
            setBody(PresignedUrlRequest(fileName = fileName, contentType = contentType))
        }.body()
}
