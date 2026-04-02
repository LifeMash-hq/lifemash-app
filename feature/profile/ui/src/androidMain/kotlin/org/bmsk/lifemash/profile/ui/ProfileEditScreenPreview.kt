package org.bmsk.lifemash.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview
@Composable
private fun ProfileEditScreenPreview() {
    LifeMashTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState(),
            onNameChange = {},
            onUsernameChange = {},
            onBioChange = {},
            onDefaultSubTabChange = {},
            onMyCalendarViewChange = {},
            onOthersCalendarViewChange = {},
            onDefaultVisibilityChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}