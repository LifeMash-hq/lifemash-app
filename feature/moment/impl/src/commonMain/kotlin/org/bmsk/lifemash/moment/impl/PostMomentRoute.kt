package org.bmsk.lifemash.moment.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.shared.common.rememberMediaPickerLauncher
import org.bmsk.lifemash.domain.moment.UploadService
import org.bmsk.lifemash.domain.moment.MediaType
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostMomentRoute(
    onSuccess: () -> Unit,
    onClose: () -> Unit,
) {
    val viewModel: PostMomentViewModel = koinViewModel()
    val uploadService: UploadService = koinInject()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showEventSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is PostMomentUiState.Success) onSuccess()
    }

    val pickMedia = rememberMediaPickerLauncher(maxItems = 10) { picked ->
        picked.forEach { media ->
            val mediaType = if (media.mimeType?.startsWith("video") == true) {
                MediaType.VIDEO
            } else {
                MediaType.IMAGE
            }
            viewModel.onAddMedia(
                uri = media.uri,
                mediaType = mediaType,
                durationMs = media.durationMs,
                width = media.width,
                height = media.height,
            )
        }
    }

    PostMomentScreen(
        form = form,
        uiState = uiState,
        onCaptionChange = viewModel::onCaptionChange,
        onCycleVisibility = viewModel::onCycleVisibility,
        onTagEventClick = { showEventSheet = true },
        onAddMedia = { pickMedia() },
        onRemoveMedia = viewModel::onRemoveMedia,
        onSubmit = {
            scope.launch {
                viewModel.onSubmit { media ->
                    uploadService.upload(media.localUri)
                }
            }
        },
        onClose = onClose,
    )

    if (showEventSheet) {
        EventSelectSheet(
            viewModel = viewModel,
            onDismiss = { showEventSheet = false },
        )
    }
}
