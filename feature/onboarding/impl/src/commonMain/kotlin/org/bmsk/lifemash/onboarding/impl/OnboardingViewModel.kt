package org.bmsk.lifemash.onboarding.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.onboarding.HandleValidationStatus
import org.bmsk.lifemash.domain.usecase.onboarding.CheckHandleUseCase
import org.bmsk.lifemash.domain.usecase.onboarding.SaveOnboardingProfileUseCase

private val HANDLE_REGEX = "^[a-z0-9_]{3,15}$".toRegex()

internal class OnboardingViewModel(
    private val checkHandle: CheckHandleUseCase,
    private val saveOnboardingProfile: SaveOnboardingProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState.Default)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var handleCheckJob: Job? = null

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun updateHandle(value: String) {
        val sanitized = value.lowercase().filter { it.isLetterOrDigit() || it == '_' }.take(15)
        val status = if (sanitized.length < 3) HandleValidationStatus.IDLE
                     else HandleValidationStatus.CHECKING
        _uiState.update { it.copy(handle = sanitized, handleStatus = status) }
        if (sanitized.length >= 3) {
            handleCheckJob?.cancel()
            handleCheckJob = viewModelScope.launch {
                delay(500)
                if (!HANDLE_REGEX.matches(sanitized)) {
                    _uiState.update { it.copy(handleStatus = HandleValidationStatus.INVALID_FORMAT) }
                    return@launch
                }
                _uiState.update { it.copy(handleStatus = HandleValidationStatus.CHECKING) }
                runCatching { checkHandle(sanitized) }
                    .onSuccess { isAvailable ->
                        _uiState.update {
                            it.copy(
                                handleStatus = if (isAvailable)
                                    HandleValidationStatus.AVAILABLE
                                else
                                    HandleValidationStatus.TAKEN
                            )
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(handleStatus = HandleValidationStatus.IDLE) }
                    }
            }
        }
    }

    fun updateBirthDate(value: String) {
        _uiState.update { it.copy(birthDate = value) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (!state.isProfileValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                saveOnboardingProfile(
                    nickname = state.name,
                    username = state.handle,
                    birthDate = state.birthDate.ifBlank { null },
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, step = OnboardingStep.PERMISSIONS) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun updateCalendarConnected(connected: Boolean) {
        _uiState.update { it.copy(calendarConnected = connected) }
    }

    fun updateNotificationAllowed(allowed: Boolean) {
        _uiState.update { it.copy(notificationAllowed = allowed) }
    }

    fun stepBack() {
        _uiState.update { it.copy(step = OnboardingStep.PROFILE_SETUP) }
    }
}
