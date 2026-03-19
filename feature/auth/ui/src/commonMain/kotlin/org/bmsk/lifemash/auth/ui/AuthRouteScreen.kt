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

    val launchKakaoLogin = rememberKakaoLoginLauncher { result ->
        result.onSuccess { accessToken ->
            viewModel.signInWithKakao(accessToken)
        }.onFailure { error ->
            onShowErrorSnackbar(error)
        }
    }

    AuthScreen(
        uiState = uiState,
        onKakaoSignIn = launchKakaoLogin,
        onGoogleSignIn = { viewModel.signInWithGoogle("placeholder-google-token") },
    )
}
