package org.bmsk.lifemash.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.moment.UploadService
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.usecase.profile.GetProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.GetUserProfileUseCase
import org.bmsk.lifemash.domain.usecase.profile.UpdateProfileSettingsUseCase
import org.bmsk.lifemash.domain.usecase.profile.UpdateProfileUseCase

internal class ProfileEditViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getProfileSettingsUseCase: GetProfileSettingsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateProfileSettingsUseCase: UpdateProfileSettingsUseCase,
    private val uploadService: UploadService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditUiState.Default)
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            runCatching {
                val settings = getProfileSettingsUseCase()
                getUserProfileUseCase("me").collect { p ->
                    _uiState.update {
                        it.copy(
                            name = p.nickname,
                            username = p.username.orEmpty(),
                            bio = p.bio.orEmpty(),
                            profileImageUrl = p.profileImage,
                            defaultSubTab = if (settings.defaultSubTab == "calendar") 1 else 0,
                            myCalendarView = if (settings.myCalendarViewMode == "chip") 1 else 0,
                            othersCalendarView = if (settings.othersCalendarViewMode == "chip") 1 else 0,
                            defaultVisibility = when (settings.defaultEventVisibility) {
                                "friend" -> 1
                                "private" -> 2
                                else -> 0
                            },
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun updateBio(value: String) {
        _uiState.update { it.copy(bio = value) }
    }

    fun updateDefaultSubTab(index: Int) {
        _uiState.update { it.copy(defaultSubTab = index) }
    }

    fun updateMyCalendarView(index: Int) {
        _uiState.update { it.copy(myCalendarView = index) }
    }

    fun updateOthersCalendarView(index: Int) {
        _uiState.update { it.copy(othersCalendarView = index) }
    }

    fun updateDefaultVisibility(index: Int) {
        _uiState.update { it.copy(defaultVisibility = index) }
    }

    fun uploadAndUpdateImage(localUri: String) {
        viewModelScope.launch {
            runCatching {
                uploadService.upload(localUri)
            }.onSuccess { url ->
                _uiState.update { it.copy(profileImageUrl = url) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun save() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            runCatching {
                updateProfileUseCase(
                    nickname = state.name.takeIf { it.isNotBlank() },
                    bio = state.bio.takeIf { it.isNotBlank() },
                    profileImage = state.profileImageUrl,
                )
                updateProfileSettingsUseCase(
                    ProfileSettings(
                        defaultSubTab = if (state.defaultSubTab == 1) "calendar" else "moments",
                        myCalendarViewMode = if (state.myCalendarView == 1) "chip" else "dot",
                        othersCalendarViewMode = if (state.othersCalendarView == 1) "chip" else "dot",
                        defaultEventVisibility = when (state.defaultVisibility) {
                            1 -> "friend"
                            2 -> "private"
                            else -> "public"
                        },
                    )
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, event = ProfileEditEvent.Saved) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun consumeEvent() {
        _uiState.update { it.copy(event = null) }
    }
}
