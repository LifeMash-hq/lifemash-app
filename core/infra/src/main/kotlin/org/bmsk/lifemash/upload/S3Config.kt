package org.bmsk.lifemash.upload

data class S3Config(
    val bucketName: String,
    val region: String,
) {
    fun publicUrl(key: String): String =
        "https://$bucketName.s3.$region.amazonaws.com/$key"

    companion object {
        fun fromEnv(): S3Config = S3Config(
            bucketName = System.getenv("S3_BUCKET_NAME") ?: "lifemash-uploads",
            region = System.getenv("S3_REGION") ?: "ap-northeast-2",
        )
    }
}
