package org.bmsk.lifemash.calendar.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bmsk.lifemash.domain.calendar.EventVisibility

@Composable
internal fun EventCreateScreen(
    uiState: EventCreateUiState,
    isEdit: Boolean = false,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTitleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onColorSelect: (String?) -> Unit,
    onVisibilitySelect: (EventVisibility) -> Unit,
    onDateTimeChange: (EventDateTime) -> Unit,
    onSwitchTab: (EventCreateTab) -> Unit,
    onShowVisibilitySheet: () -> Unit,
    onDismissVisibilitySheet: () -> Unit,
    onConfirmLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.activeTab) {
        EventCreateTab.FORM -> EventFormContent(
            uiState = uiState,
            isEdit = isEdit,
            onCancel = onCancel,
            onSave = onSave,
            onTitleChange = onTitleChange,
            onLocationChange = onLocationChange,
            onMemoChange = onMemoChange,
            onColorSelect = onColorSelect,
            onSwitchTab = onSwitchTab,
            onShowVisibilitySheet = onShowVisibilitySheet,
            modifier = modifier,
        )

        EventCreateTab.DATE_TIME -> DateTimePickerContent(
            dateTime = uiState.eventDateTime,
            onDateTimeChange = onDateTimeChange,
            onBack = { onSwitchTab(EventCreateTab.FORM) },
        )

        EventCreateTab.LOCATION -> LocationInputContent(
            location = uiState.location,
            onLocationChange = onLocationChange,
            onConfirm = onConfirmLocation,
            onBack = { onSwitchTab(EventCreateTab.FORM) },
        )
    }

    if (uiState.isVisibilitySheetVisible) {
        VisibilitySheet(
            currentVisibility = uiState.visibility,
            onSelect = onVisibilitySelect,
            onDismiss = onDismissVisibilitySheet,
        )
    }
}
