package org.bmsk.lifemash.auth.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview(name = "Light - Idle", showBackground = true)
@Preview(name = "Dark - Idle", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun AuthScreenPreview_Idle() {
    LifeMashTheme {
        AuthScreen(
            uiState = AuthUiState.Idle,
            onBackClick = {},
            onKakaoSignIn = {},
            onGoogleSignIn = {},
            onEmailSignIn = { _, _ -> },
        )
    }
}

@Preview(name = "Light - Loading", showBackground = true)
@Preview(name = "Dark - Loading", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun AuthScreenPreview_Loading() {
    LifeMashTheme {
        AuthScreen(
            uiState = AuthUiState.Loading,
            onBackClick = {},
            onKakaoSignIn = {},
            onGoogleSignIn = {},
            onEmailSignIn = { _, _ -> },
        )
    }
}

@Preview(name = "Light - Error", showBackground = true)
@Preview(name = "Dark - Error", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun AuthScreenPreview_Error() {
    LifeMashTheme {
        AuthScreen(
            uiState = AuthUiState.Error("이메일 또는 비밀번호가 올바르지 않습니다."),
            onBackClick = {},
            onKakaoSignIn = {},
            onGoogleSignIn = {},
            onEmailSignIn = { _, _ -> },
        )
    }
}

@Preview(name = "Light - Welcome", showBackground = true)
@Preview(name = "Dark - Welcome", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun WelcomeScreenPreview() {
    LifeMashTheme {
        WelcomeScreen(
            onStartClick = {},
            onLoginClick = {},
        )
    }
}
