package org.bmsk.lifemash.eventdetail.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        val imageEmoji: String = "📅",
        val sharedByNickname: String? = null,
        val attendees: List<Attendee> = emptyList(),
        val comments: List<Comment> = emptyList(),
        val isJoined: Boolean = false,
    ) : EventDetailUiState
    data class Error(val message: String) : EventDetailUiState
}

class EventDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState

    fun loadEvent(eventId: String) {
        _uiState.value = EventDetailUiState.Loaded(
            eventId = eventId,
            title = "청담 오마카세",
            date = "2026년 4월 5일 토요일 19:00",
            location = "서울 강남구 청담동 101",
            description = "친구들과 특별한 오마카세 디너 🍣",
            imageEmoji = "🍣",
            sharedByNickname = "이수아",
            attendees = listOf(
                Attendee("1", "이수아"),
                Attendee("2", "박현우"),
                Attendee("3", "정재원"),
                Attendee("4", "김민지"),
                Attendee("5", "최준혁"),
                Attendee("6", "한소희"),
            ),
            comments = listOf(
                Comment("c1", "이수아", "너무 기대된다!! 🎉", "2일 전"),
                Comment("c2", "박현우", "저 거기 가봤는데 진짜 맛있어요", "1일 전"),
                Comment("c3", "정재원", "드레스코드 있나요?", "3시간 전"),
            ),
            isJoined = true,
        )
    }

    fun toggleJoin(loaded: EventDetailUiState.Loaded) {
        _uiState.value = loaded.copy(isJoined = !loaded.isJoined)
    }

    fun addComment(loaded: EventDetailUiState.Loaded, content: String) {
        _uiState.value = loaded.copy(
            comments = loaded.comments + Comment(
                id = "new_${loaded.comments.size}",
                authorNickname = "나",
                content = content,
                createdAt = "방금",
            )
        )
    }
}
