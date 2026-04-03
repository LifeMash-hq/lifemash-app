package org.bmsk.lifemash.moment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.domain.model.MomentMedia
import org.bmsk.lifemash.moment.domain.model.Visibility
import org.bmsk.lifemash.moment.domain.usecase.CreateMomentUseCase

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
internal class PostMomentViewModel(
    private val createMoment: CreateMomentUseCase,
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(PostMomentFormState())
    val form: StateFlow<PostMomentFormState> = _form.asStateFlow()

    private val _uiState = MutableStateFlow<PostMomentUiState>(PostMomentUiState.Idle)
    val uiState: StateFlow<PostMomentUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            runCatching {
                val groups = groupRepository.getMyGroups()
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                groups.flatMap { group ->
                    eventRepository.getMonthEvents(group.id, now.year, now.month.number)
                }.sortedBy { it.startAt }
            }.onSuccess { events ->
                _events.value = events
            }
        }
    }

    fun onCaptionChange(value: String) {
        _form.update { it.copy(caption = value.take(200)) }
    }

    fun onCycleVisibility() {
        _form.update { it.copy(visibility = Visibility.next(it.visibility)) }
    }

    fun onTagEvent(eventId: String?, eventTitle: String?) {
        _form.update { it.copy(eventId = eventId, eventTitle = eventTitle) }
    }

    fun onAddMedia(
        uri: String,
        mediaType: MediaType,
        durationMs: Long? = null,
        width: Int? = null,
        height: Int? = null,
    ) {
        _form.update { state ->
            if (state.isMediaFull) return@update state
            val item = SelectedMedia(
                id = kotlin.uuid.Uuid.random().toString(),
                localUri = uri,
                mediaType = mediaType,
                durationMs = durationMs,
                width = width,
                height = height,
            )
            state.copy(media = state.media + item)
        }
    }

    fun onRemoveMedia(id: String) {
        _form.update { it.copy(media = it.media.filter { m -> m.id != id }) }
    }

    fun onSubmit(uploadMedia: suspend (SelectedMedia) -> String) {
        val state = _form.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _uiState.value = PostMomentUiState.Uploading(0f)
            runCatching {
                val total = state.media.size.coerceAtLeast(1).toFloat()
                val uploadedMedia = state.media.mapIndexed { index, item ->
                    val url = uploadMedia(item)
                    _uiState.value = PostMomentUiState.Uploading((index + 1f) / total)
                    MomentMedia(
                        mediaUrl = url,
                        mediaType = item.mediaType,
                        sortOrder = index,
                        width = item.width,
                        height = item.height,
                        durationMs = item.durationMs,
                    )
                }

                createMoment(
                    eventId = state.eventId,
                    caption = state.caption.ifBlank { null },
                    visibility = state.visibility,
                    media = uploadedMedia,
                )
                _uiState.value = PostMomentUiState.Success
            }.onFailure { e ->
                _uiState.value = PostMomentUiState.Error(e.message ?: "순간 올리기에 실패했습니다")
            }
        }
    }

    fun onErrorDismissed() {
        _uiState.value = PostMomentUiState.Idle
    }
}
