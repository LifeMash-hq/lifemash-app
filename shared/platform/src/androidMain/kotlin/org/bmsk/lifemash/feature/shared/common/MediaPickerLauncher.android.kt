package org.bmsk.lifemash.feature.shared.common

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberMediaPickerLauncher(
    maxItems: Int,
    onResult: (List<PickedMedia>) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems),
    ) { uris ->
        onResult(uris.map { uri -> uri.toPickedMedia(context) })
    }
    return remember(launcher) {
        { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }
    }
}

@Composable
actual fun rememberSingleImagePickerLauncher(
    onResult: (PickedMedia?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        onResult(uri?.toPickedMedia(context))
    }
    return remember(launcher) {
        { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    }
}

private fun Uri.toPickedMedia(context: Context): PickedMedia {
    val mimeType = context.contentResolver.getType(this)
    var width: Int? = null
    var height: Int? = null
    var durationMs: Long? = null

    runCatching {
        context.contentResolver.query(
            this,
            arrayOf(
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION,
            ),
            null, null, null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val wIdx = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH)
                val hIdx = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT)
                val dIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DURATION)
                if (wIdx >= 0) width = cursor.getInt(wIdx).takeIf { it > 0 }
                if (hIdx >= 0) height = cursor.getInt(hIdx).takeIf { it > 0 }
                if (dIdx >= 0) durationMs = cursor.getLong(dIdx).takeIf { it > 0 }
            }
        }
    }

    return PickedMedia(
        uri = this.toString(),
        mimeType = mimeType,
        width = width,
        height = height,
        durationMs = durationMs,
    )
}
