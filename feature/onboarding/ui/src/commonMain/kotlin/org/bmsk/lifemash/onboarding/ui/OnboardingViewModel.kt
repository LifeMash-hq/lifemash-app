package org.bmsk.lifemash.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.onboarding.domain.model.HandleValidationStatus
import org.bmsk.lifemash.onboarding.domain.repository.OnboardingRepository

private val HANDLE_REGEX = "^[a-z0-9_]{3,15}$".toRegex()

@OptIn(FlowPreview::class)
internal class OnboardingViewModel(
    private val repository: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val handleInput = MutableSharedFlow<String>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            handleInput
                .debounce(500)
                .filter { it.length >= 3 }
                .collectLatest { handle ->
                    if (!HANDLE_REGEX.matches(handle)) {
                        _uiState.update { it.copy(handleStatus = HandleValidationStatus.INVALID_FORMAT) }
                        return@collectLatest
                    }
                    _uiState.update { it.copy(handleStatus = HandleValidationStatus.CHECKING) }
                    runCatching { repository.checkHandle(handle) }
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

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun updateHandle(value: String) {
        val sanitized = value.lowercase().filter { it.isLetterOrDigit() || it == '_' }.take(15)
        val status = if (sanitized.length < 3) HandleValidationStatus.IDLE
                     else HandleValidationStatus.CHECKING
        _uiState.update { it.copy(handle = sanitized, handleStatus = status) }
        if (sanitized.length >= 3) {
            handleInput.tryEmit(sanitized)
        }
    }

    fun updateBirthDate(value: String) {
        _uiState.update { it.copy(birthDate = value) }
    }

    fun saveProfile(onComplete: () -> Unit) {
        val state = _uiState.value
        if (!state.isProfileValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                repository.saveProfile(
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
