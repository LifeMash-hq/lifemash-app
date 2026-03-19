package org.bmsk.lifemash.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.usecase.GetCurrentUserUseCase

class MainViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    val currentUser: StateFlow<AuthUser?> = getCurrentUserUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
