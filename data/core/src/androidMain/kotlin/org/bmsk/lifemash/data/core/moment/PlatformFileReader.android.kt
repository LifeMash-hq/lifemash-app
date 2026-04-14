package org.bmsk.lifemash.data.core.moment

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import org.koin.core.context.GlobalContext

internal actual fun createPlatformFileReader(): PlatformFileReader {
    val context = GlobalContext.get().get<Context>()
    return AndroidFileReader(context)
}

private class AndroidFileReader(private val context: Context) : PlatformFileReader {

    override suspend fun readBytes(uri: String): ByteArray =
        context.contentResolver.openInputStream(Uri.parse(uri))?.use { it.readBytes() }
            ?: error("Cannot open stream for uri: $uri")

    override fun getContentType(uri: String): String? =
        context.contentResolver.getType(Uri.parse(uri))

    override fun getFileName(uri: String): String? {
        val parsedUri = Uri.parse(uri)
        if (parsedUri.scheme == "content") {
            context.contentResolver.query(
                parsedUri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) return cursor.getString(idx)
                }
            }
        }
        return parsedUri.lastPathSegment
    }
}
