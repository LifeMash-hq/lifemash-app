package org.bmsk.lifemash.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthRouteScreen(
    onSignInComplete: () -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogin by rememberSaveable { mutableStateOf(false) }

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

    val launchGoogleLogin = rememberGoogleLoginLauncher { result ->
        result.onSuccess { idToken ->
            viewModel.signInWithGoogle(idToken)
        }.onFailure { error ->
            onShowErrorSnackbar(error)
        }
    }

    if (showLogin) {
        AuthScreen(
            uiState = uiState,
            onBackClick = { showLogin = false },
            onKakaoSignIn = launchKakaoLogin,
            onGoogleSignIn = launchGoogleLogin,
            onEmailSignIn = viewModel::signInWithEmail,
        )
    } else {
        WelcomeScreen(
            onStartClick = { showLogin = true },
            onLoginClick = { showLogin = true },
        )
    }
}
