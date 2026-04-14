package org.bmsk.lifemash.auth.impl

internal sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val isNewUser: Boolean) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
