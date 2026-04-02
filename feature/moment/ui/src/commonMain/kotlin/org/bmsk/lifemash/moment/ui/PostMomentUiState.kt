package org.bmsk.lifemash.moment.ui

import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.domain.model.Visibility

internal data class PostMomentFormState(
    val caption: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val media: List<SelectedMedia> = emptyList(),
) {
    val canSubmit: Boolean get() = caption.isNotBlank() || media.isNotEmpty()
    val isMediaFull: Boolean get() = media.size >= 10
}

internal data class SelectedMedia(
    val id: String,
    val localUri: String,
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    /** S3 업로드 완료 후 채워짐 */
    val uploadedUrl: String? = null,
)

internal sealed interface PostMomentUiState {
    data object Idle : PostMomentUiState
    data class Uploading(val progress: Float) : PostMomentUiState
    data object Success : PostMomentUiState
    data class Error(val message: String) : PostMomentUiState
}
