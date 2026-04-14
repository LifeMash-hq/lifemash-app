package org.bmsk.lifemash.profile.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview
@Composable
private fun ProfileEditScreenPreview() {
    LifeMashTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState.Default,
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