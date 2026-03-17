package org.bmsk.lifemash.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bmsk.lifemash.auth.domain.usecase.SignInWithGoogleUseCase
import org.bmsk.lifemash.auth.domain.usecase.SignInWithKakaoUseCase

internal class AuthViewModel(
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithKakao(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { signInWithKakaoUseCase(accessToken) }
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { signInWithGoogleUseCase(idToken) }
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }
}
