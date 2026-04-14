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

    println("[AuthDiag] AuthRouteScreen recompose, uiState=$uiState")

    LaunchedEffect(uiState) {
        println("[AuthDiag] LaunchedEffect uiState=$uiState")
        val state = uiState
        if (state is AuthUiState.Success) {
            println("[AuthDiag] → onSignInComplete(isNewUser=${state.isNewUser})")
            onSignInComplete(state.isNewUser)
            println("[AuthDiag] → onSignInComplete returned")
        }
    }

    val launchKakaoLogin = rememberKakaoLoginLauncher { result ->
        println("[AuthDiag] KakaoCallback result=$result")
        result.onSuccess { accessToken ->
            println("[AuthDiag] → signInWithKakao()")
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
