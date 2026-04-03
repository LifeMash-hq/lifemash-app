package org.bmsk.lifemash.model.upload

interface UploadService {
    /** localUri를 S3에 업로드하고 공개 URL을 반환한다. */
    suspend fun upload(localUri: String): String
}
