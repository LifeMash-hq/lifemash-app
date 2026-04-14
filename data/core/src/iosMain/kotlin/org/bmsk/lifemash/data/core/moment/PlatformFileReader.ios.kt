package org.bmsk.lifemash.data.core.moment

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

internal actual fun createPlatformFileReader(): PlatformFileReader = IosFileReader()

private class IosFileReader : PlatformFileReader {

    override suspend fun readBytes(uri: String): ByteArray {
        val url = NSURL.URLWithString(uri) ?: NSURL.fileURLWithPath(uri)
        val data = NSData.dataWithContentsOfURL(url)
            ?: error("Cannot read data from uri: $uri")
        return data.toByteArray()
    }

    override fun getContentType(uri: String): String? {
        val lower = uri.lowercase()
        return when {
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            lower.endsWith(".png") -> "image/png"
            lower.endsWith(".heic") -> "image/heic"
            lower.endsWith(".gif") -> "image/gif"
            lower.endsWith(".webp") -> "image/webp"
            lower.endsWith(".mp4") -> "video/mp4"
            lower.endsWith(".mov") -> "video/quicktime"
            lower.endsWith(".m4v") -> "video/x-m4v"
            else -> null
        }
    }

    override fun getFileName(uri: String): String? =
        NSURL.URLWithString(uri)?.lastPathComponent
            ?: NSURL.fileURLWithPath(uri).lastPathComponent
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    return this.bytes?.readBytes(length) ?: ByteArray(0)
}
