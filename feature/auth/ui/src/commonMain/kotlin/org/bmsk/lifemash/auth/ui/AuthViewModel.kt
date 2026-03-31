package org.bmsk.lifemash.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bmsk.lifemash.auth.domain.repository.AuthRepository

internal class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithKakao(accessToken: String) {
        println("KakaoLogin: signInWithKakao called, token=${accessToken.take(10)}...")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { authRepository.signInWithKakao(accessToken) }
                .onSuccess {
                    println("KakaoLogin: signIn SUCCESS")
                    _uiState.value = AuthUiState.Success
                }
                .onFailure {
                    println("KakaoLogin: signIn FAILURE: ${it.message}")
                    _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패")
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { authRepository.signInWithGoogle(idToken) }
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { authRepository.signInWithEmail(email, password) }
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "로그인 실패") }
        }
    }
}
