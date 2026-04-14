package org.bmsk.lifemash.calendar.impl

import org.bmsk.lifemash.domain.calendar.EventVisibility

enum class EventCreateTab {
    FORM, DATE_TIME, LOCATION
}

data class EventCreateUiState(
    val activeTab: EventCreateTab,
    val title: String,
    val location: String,
    val selectedColor: String?,
    val visibility: EventVisibility,
    val memo: String,
    val eventDateTime: EventDateTime,
    val isVisibilitySheetVisible: Boolean,
    val isSaving: Boolean,
    val errorMessage: String?,
    val event: EventCreateEvent?,
) {
    val isSaveEnabled: Boolean by lazy { title.isNotBlank() }

    fun withTitle(value: String) = copy(title = value)
    fun withLocation(value: String) = copy(location = value)
    fun withMemo(value: String) = copy(memo = value)

    companion object {
        val Default = EventCreateUiState(
            activeTab = EventCreateTab.FORM,
            title = "",
            location = "",
            selectedColor = null,
            visibility = EventVisibility.Followers,
            memo = "",
            eventDateTime = EventDateTime.now(),
            isVisibilitySheetVisible = false,
            isSaving = false,
            errorMessage = null,
            event = null,
        )
    }
}

sealed interface EventCreateEvent {
    data object Saved : EventCreateEvent
}
