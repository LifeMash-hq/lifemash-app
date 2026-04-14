package org.bmsk.lifemash.moment.impl

import org.bmsk.lifemash.domain.moment.MediaType
import org.bmsk.lifemash.domain.moment.Visibility

internal data class PostMomentFormState(
    val caption: String,
    val visibility: Visibility,
    val eventId: String?,
    val eventTitle: String?,
    val media: List<SelectedMedia>,
) {
    val canSubmit: Boolean get() = caption.isNotBlank() || media.isNotEmpty()
    val isMediaFull: Boolean get() = media.size >= 10

    companion object {
        val Default = PostMomentFormState(
            caption = "",
            visibility = Visibility.PUBLIC,
            eventId = null,
            eventTitle = null,
            media = emptyList(),
        )
    }
}

internal data class SelectedMedia(
    val id: String,
    val localUri: String,
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val uploadedUrl: String? = null,
)

internal sealed interface PostMomentUiState {
    data object Idle : PostMomentUiState
    data class Uploading(val progress: Float) : PostMomentUiState
    data object Success : PostMomentUiState
    data class Error(val message: String) : PostMomentUiState
}
