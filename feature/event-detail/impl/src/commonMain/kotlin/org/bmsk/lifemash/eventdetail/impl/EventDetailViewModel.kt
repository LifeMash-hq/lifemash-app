@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.eventdetail.impl

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.number
import org.bmsk.lifemash.domain.eventdetail.EventAttendee
import org.bmsk.lifemash.domain.eventdetail.EventComment
import org.bmsk.lifemash.domain.usecase.eventdetail.AddEventCommentUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.GetEventDetailUseCase
import org.bmsk.lifemash.domain.usecase.eventdetail.ToggleEventJoinUseCase
import kotlin.time.Instant

internal class EventDetailViewModel(
    private val getEventDetail: GetEventDetailUseCase,
    private val toggleEventJoin: ToggleEventJoinUseCase,
    private val addEventComment: AddEventCommentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            runCatching { getEventDetail(eventId) }
                .onSuccess { detail ->
                    _uiState.value = EventDetailUiState.Loaded(
                        eventId = detail.id,
                        title = detail.title,
                        date = detail.startAt.formatDate(),
                        startAt = detail.startAt,
                        endAt = detail.endAt,
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
            runCatching { toggleEventJoin(loaded.eventId) }
                .onFailure {
                    _uiState.value = loaded.copy(isJoined = loaded.isJoined)
                }
        }
    }

    fun addComment(loaded: EventDetailUiState.Loaded, content: String) {
        viewModelScope.launch {
            runCatching { addEventComment(loaded.eventId, content) }
                .onSuccess { comment ->
                    _uiState.value = loaded.copy(
                        comments = loaded.comments + comment.toUi(),
                    )
                }
        }
    }
}

private fun EventAttendee.toUi() = Attendee(
    id = id,
    nickname = nickname,
    profileImage = profileImage,
)
private fun EventComment.toUi() = Comment(
    id = id,
    authorNickname = authorNickname,
    content = content,
    createdAt = createdAt,
)

@OptIn(kotlin.time.ExperimentalTime::class)
private fun Instant.formatDate(): String {
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = if (local.hour < 12) "오전 ${local.hour}" else "오후 ${local.hour - 12}"
    val min = local.minute.toString().padStart(2, '0')
    return "${local.year}년 ${local.month.number}월 ${local.day}일 $hour:$min"
}
