package org.bmsk.lifemash.upload

import org.bmsk.lifemash.model.upload.PresignedUrlResponse

interface UploadService {
    suspend fun generatePresignedUrl(fileName: String, contentType: String): PresignedUrlResponse
}
