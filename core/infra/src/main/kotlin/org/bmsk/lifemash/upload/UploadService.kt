package org.bmsk.lifemash.upload

import org.bmsk.lifemash.model.upload.PresignedUrlResponse
import kotlin.uuid.Uuid

class UploadService {
    fun generatePresignedUrl(fileName: String, contentType: String): PresignedUrlResponse {
        val key = "uploads/${Uuid.random()}/$fileName"
        val baseUrl = System.getenv("S3_BUCKET_URL") ?: "https://lifemash-uploads.s3.ap-northeast-2.amazonaws.com"
        return PresignedUrlResponse(
            uploadUrl = "$baseUrl/$key?presigned=placeholder",
            publicUrl = "$baseUrl/$key",
        )
    }
}
