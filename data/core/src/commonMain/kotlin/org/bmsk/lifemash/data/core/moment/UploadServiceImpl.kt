package org.bmsk.lifemash.data.core.moment

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import org.bmsk.lifemash.domain.moment.UploadService
import org.bmsk.lifemash.data.remote.moment.UploadApi

internal class UploadServiceImpl(
    private val uploadApi: UploadApi,
    private val uploadClient: HttpClient,
) : UploadService {

    private val fileReader: PlatformFileReader by lazy { createPlatformFileReader() }

    override suspend fun upload(localUri: String): String {
        val bytes = fileReader.readBytes(localUri)
        val contentType = fileReader.getContentType(localUri) ?: "application/octet-stream"
        val fileName = fileReader.getFileName(localUri)
            ?: "upload_${(0..999999).random()}"

        val presigned = uploadApi.getPresignedUrl(fileName, contentType)

        uploadClient.put(presigned.uploadUrl) {
            header(HttpHeaders.ContentType, contentType)
            setBody(bytes)
        }

        return presigned.publicUrl
    }
}
