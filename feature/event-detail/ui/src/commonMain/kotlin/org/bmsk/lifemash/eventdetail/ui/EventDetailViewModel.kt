package org.bmsk.lifemash.eventdetail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bmsk.lifemash.eventdetail.domain.model.EventAttendee
import org.bmsk.lifemash.eventdetail.domain.model.EventComment
import org.bmsk.lifemash.eventdetail.domain.repository.EventDetailRepository

data class Comment(
    val id: String,
    val authorNickname: String,
    val content: String,
    val createdAt: String,
)

data class Attendee(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)

sealed interface EventDetailUiState {
    data object Loading : EventDetailUiState
    data class Loaded(
        val eventId: String,
        val title: String,
        val date: String,
        val location: String? = null,
        val description: String? = null,
        val imageEmoji: String = "",
        val sharedByNickname: String? = null,
        val attendees: List<Attendee> = emptyList(),
        val comments: List<Comment> = emptyList(),
        val isJoined: Boolean = false,
    ) : EventDetailUiState
    data class Error(val message: String) : EventDetailUiState
}

internal class EventDetailViewModel(
    private val repository: EventDetailRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            runCatching { repository.getEventDetail(eventId) }
                .onSuccess { detail ->
                    _uiState.value = EventDetailUiState.Loaded(
                        eventId = detail.id,
                        title = detail.title,
                        date = detail.date,
                        location = detail.location,
                        description = detail.description,
                        imageEmoji = detail.imageEmoji,
                        sharedByNickname = detail.sharedByNickname,
                        attendees = detail.attendees.map { it.toUi() },
                        comments = detail.comments.map { it.toUi() },
                        isJoined = detail.isJoined,
                    )
                }
                .onFailure { e ->
                    _uiState.value = EventDetailUiState.Error(
                        e.message ?: "이벤트를 불러올 수 없습니다",
                    )
                }
        }
    }

    fun toggleJoin(loaded: EventDetailUiState.Loaded) {
        _uiState.value = loaded.copy(isJoined = !loaded.isJoined)
        viewModelScope.launch {
            runCatching { repository.toggleJoin(loaded.eventId) }
                .onFailure {
                    _uiState.value = loaded.copy(isJoined = loaded.isJoined)
                }
        }
    }

    fun addComment(loaded: EventDetailUiState.Loaded, content: String) {
        viewModelScope.launch {
            runCatching { repository.addComment(loaded.eventId, content) }
                .onSuccess { comment ->
                    _uiState.value = loaded.copy(
                        comments = loaded.comments + comment.toUi(),
                    )
                }
        }
    }
}

private fun EventAttendee.toUi() = Attendee(id = id, nickname = nickname, profileImage = profileImage)
private fun EventComment.toUi() = Comment(id = id, authorNickname = authorNickname, content = content, createdAt = createdAt)
