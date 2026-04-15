package org.bmsk.lifemash.auth.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.bmsk.lifemash.designsystem.component.LifeMashBackground
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

private class AuthUiStateProvider : PreviewParameterProvider<AuthUiState> {
    override val values = sequenceOf(
        AuthUiState.Idle,
        AuthUiState.Loading,
        AuthUiState.Error("이메일 또는 비밀번호가 올바르지 않습니다."),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun AuthScreenPreview(
    @PreviewParameter(AuthUiStateProvider::class) uiState: AuthUiState,
) {
    LifeMashTheme {
        LifeMashBackground {
            AuthScreen(
                uiState = uiState,
                onBackClick = {},
                onKakaoSignIn = {},
                onGoogleSignIn = {},
                onEmailSignIn = { _, _ -> },
            )
        }
    }
}
