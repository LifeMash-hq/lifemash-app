package org.bmsk.lifemash.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.usecase.auth.GetCurrentUserUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithEmailUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithGoogleUseCase
import org.bmsk.lifemash.domain.usecase.auth.SignInWithKakaoUseCase

internal class AuthViewModel(
    private val signInWithKakao: SignInWithKakaoUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithKakao(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { signInWithKakao(accessToken) }
                .onSuccess { emitSuccess() }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { signInWithGoogle(idToken) }
                .onSuccess { emitSuccess() }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { signInWithEmail(email, password) }
                .onSuccess { emitSuccess() }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }

    private suspend fun emitSuccess() {
        val user = getCurrentUser().first()
        _uiState.value = AuthUiState.Success(isNewUser = user?.username == null)
    }
}
