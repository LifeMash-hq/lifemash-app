package org.bmsk.lifemash.upload

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import org.bmsk.lifemash.model.upload.PresignedUrlResponse
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class S3UploadService(private val config: S3Config) : UploadService {
    private val client = S3Client { region = config.region }

    override suspend fun generatePresignedUrl(fileName: String, contentType: String): PresignedUrlResponse {
        val key = "uploads/${Uuid.random()}/$fileName"
        val presigned = client.presignPutObject(PutObjectRequest {
            bucket = config.bucketName
            this.key = key
            this.contentType = contentType
        }, 15.minutes)
        return PresignedUrlResponse(
            uploadUrl = presigned.url.toString(),
            publicUrl = config.publicUrl(key),
        )
    }
}
