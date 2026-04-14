package org.bmsk.lifemash.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.bmsk.lifemash.domain.auth.AuthUser
import org.bmsk.lifemash.domain.auth.AuthRepository

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: AuthUser) : AuthState
}

internal class MainViewModel(
    authRepository: AuthRepository,
) : ViewModel() {
    val authState: StateFlow<AuthState> = authRepository.getCurrentUser()
        .map { user -> if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AuthState.Loading,
        )
}
