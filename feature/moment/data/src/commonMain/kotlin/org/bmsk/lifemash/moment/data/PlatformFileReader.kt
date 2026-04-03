package org.bmsk.lifemash.moment.data

internal interface PlatformFileReader {
    suspend fun readBytes(uri: String): ByteArray
    fun getContentType(uri: String): String?
    fun getFileName(uri: String): String?
}

internal expect fun createPlatformFileReader(): PlatformFileReader
