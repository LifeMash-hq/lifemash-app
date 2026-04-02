package org.bmsk.lifemash.memo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview
@Composable
private fun MemoScreenPreview() {
    LifeMashTheme {
        MemoScreen(
            uiState = MemoUiState(
                isLoading = false
            )
        )
    }
}