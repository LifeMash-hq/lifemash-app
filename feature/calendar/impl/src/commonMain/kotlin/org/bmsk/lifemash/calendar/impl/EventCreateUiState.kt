package org.bmsk.lifemash.calendar.impl

data class EventCreateUiState(
    val isSaving: Boolean,
    val errorMessage: String?,
    val event: EventCreateEvent?,
) {
    companion object {
        val Default = EventCreateUiState(
            isSaving = false,
            errorMessage = null,
            event = null,
        )
    }
}

sealed interface EventCreateEvent {
    data object Saved : EventCreateEvent
}
