package org.bmsk.lifemash.calendar.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview(name = "Light - 새 일정", showBackground = true)
@Preview(name = "Dark - 새 일정", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun EventCreateScreenPreview_Create() {
    LifeMashTheme {
        EventCreateScreen(
            uiState = EventCreateUiState.Default,
            onCancel = {},
            onSave = {},
            onTitleChange = {},
            onLocationChange = {},
            onMemoChange = {},
            onColorSelect = {},
            onVisibilitySelect = {},
            onDateTimeChange = {},
            onSwitchTab = {},
            onShowVisibilitySheet = {},
            onDismissVisibilitySheet = {},
            onConfirmLocation = {},
        )
    }
}

@Preview(name = "Light - 저장 중", showBackground = true)
@Preview(name = "Dark - 저장 중", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun EventCreateScreenPreview_Saving() {
    LifeMashTheme {
        EventCreateScreen(
            uiState = EventCreateUiState.Default.copy(
                title = "팀 회식",
                isSaving = true,
            ),
            onCancel = {},
            onSave = {},
            onTitleChange = {},
            onLocationChange = {},
            onMemoChange = {},
            onColorSelect = {},
            onVisibilitySelect = {},
            onDateTimeChange = {},
            onSwitchTab = {},
            onShowVisibilitySheet = {},
            onDismissVisibilitySheet = {},
            onConfirmLocation = {},
        )
    }
}
