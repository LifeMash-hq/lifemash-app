package org.bmsk.lifemash.calendar.ui

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
            uiState = EventCreateUiState(),
            year = 2024,
            month = 3,
            day = 15,
            onSave = { _, _, _, _, _, _ -> },
            onCancel = {},
        )
    }
}

@Preview(name = "Light - 일정 수정", showBackground = true)
@Preview(name = "Dark - 일정 수정", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun EventCreateScreenPreview_Edit() {
    LifeMashTheme {
        EventCreateScreen(
            uiState = EventCreateUiState(),
            year = 2024,
            month = 3,
            day = 15,
            existingEvent = sampleEvents.first(),
            onSave = { _, _, _, _, _, _ -> },
            onCancel = {},
        )
    }
}

@Preview(name = "Light - 저장 중", showBackground = true)
@Preview(name = "Dark - 저장 중", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun EventCreateScreenPreview_Saving() {
    LifeMashTheme {
        EventCreateScreen(
            uiState = EventCreateUiState(isSaving = true),
            year = 2024,
            month = 3,
            day = 15,
            onSave = { _, _, _, _, _, _ -> },
            onCancel = {},
        )
    }
}
