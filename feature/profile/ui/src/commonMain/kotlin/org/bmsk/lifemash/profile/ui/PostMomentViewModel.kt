package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bmsk.lifemash.profile.domain.repository.MomentRepository

sealed interface PostMomentEvent {
    data object PostComplete : PostMomentEvent
    data class PostError(val message: String) : PostMomentEvent
    data class ValidationError(val message: String) : PostMomentEvent
}

enum class Visibility { PUBLIC, FOLLOWERS, PRIVATE }

data class PostMomentUiState(
    val eventId: String = "",
    val eventTitle: String = "",
    val media: List<String> = emptyList(),
    val caption: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val isSubmitting: Boolean = false,
)

internal class PostMomentViewModel(
    private val momentRepository: MomentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostMomentUiState())
    val uiState: StateFlow<PostMomentUiState> = _uiState

    private val _events = MutableSharedFlow<PostMomentEvent>()
    val events: SharedFlow<PostMomentEvent> = _events

    fun init(eventId: String, eventTitle: String) {
        _uiState.value = _uiState.value.copy(eventId = eventId, eventTitle = eventTitle)
    }

    fun addMedia(uri: String) {
        _uiState.value = _uiState.value.copy(media = _uiState.value.media + uri)
    }

    fun removeMedia(index: Int) {
        _uiState.value = _uiState.value.copy(media = _uiState.value.media.toMutableList().also { it.removeAt(index) })
    }

    fun cycleVisibility() {
        val next = when (_uiState.value.visibility) {
            Visibility.PUBLIC -> Visibility.FOLLOWERS
            Visibility.FOLLOWERS -> Visibility.PRIVATE
            Visibility.PRIVATE -> Visibility.PUBLIC
        }
        _uiState.value = _uiState.value.copy(visibility = next)
    }

    fun submit() {
        val state = _uiState.value
        if (state.media.isEmpty()) {
            viewModelScope.launch { _events.emit(PostMomentEvent.ValidationError("미디어를 추가해주세요")) }
            return
        }
        _uiState.value = state.copy(isSubmitting = true)
        viewModelScope.launch {
            runCatching {
                momentRepository.postMoment(
                    eventId = state.eventId,
                    imageUrl = state.media.first(),
                    caption = state.caption.ifBlank { null },
                    visibility = state.visibility.name.lowercase(),
                )
                _events.emit(PostMomentEvent.PostComplete)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                _events.emit(PostMomentEvent.PostError(it.message ?: "오류가 발생했습니다"))
            }
        }
    }
}
