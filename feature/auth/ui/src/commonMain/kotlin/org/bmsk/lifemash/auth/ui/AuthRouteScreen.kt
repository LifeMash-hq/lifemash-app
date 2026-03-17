package org.bmsk.lifemash.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthRouteScreen(
    onSignInComplete: () -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSignInComplete()
        }
    }

    AuthScreen(
        uiState = uiState,
        // TODO: 실제 소셜 SDK 연동 시 토큰을 받아서 전달
        // 현재는 placeholder — 소셜 SDK expect/actual 구현 필요
        onKakaoSignIn = { viewModel.signInWithKakao("placeholder-kakao-token") },
        onGoogleSignIn = { viewModel.signInWithGoogle("placeholder-google-token") },
    )
}
