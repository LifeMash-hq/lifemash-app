package org.bmsk.lifemash.auth.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthRouteScreen(
    onSignInComplete: (isNewUser: Boolean) -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogin by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is AuthUiState.Success) {
            onSignInComplete(state.isNewUser)
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
