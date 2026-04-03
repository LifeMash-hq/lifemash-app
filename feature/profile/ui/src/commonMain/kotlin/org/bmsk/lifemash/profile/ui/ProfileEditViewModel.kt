package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.profile.domain.model.ProfileSettings
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

data class ProfileEditUiState(
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val profileImageUrl: String? = null,
    val defaultSubTab: Int = 0,        // 0=순간, 1=캘린더
    val myCalendarView: Int = 0,       // 0=점, 1=칩
    val othersCalendarView: Int = 0,   // 0=점, 1=칩
    val defaultVisibility: Int = 0,    // 0=전체, 1=친구, 2=비공개
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

internal class ProfileEditViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState

    fun load() {
        viewModelScope.launch {
            runCatching {
                val profile = profileRepository.getProfile("me")
                val settings = profileRepository.getProfileSettings()
                profile.collect { p ->
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

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateUsername(value: String) = _uiState.update { it.copy(username = value) }
    fun updateBio(value: String) = _uiState.update { it.copy(bio = value) }
    fun updateDefaultSubTab(index: Int) = _uiState.update { it.copy(defaultSubTab = index) }
    fun updateMyCalendarView(index: Int) = _uiState.update { it.copy(myCalendarView = index) }
    fun updateOthersCalendarView(index: Int) = _uiState.update { it.copy(othersCalendarView = index) }
    fun updateDefaultVisibility(index: Int) = _uiState.update { it.copy(defaultVisibility = index) }
    fun updateProfileImage(url: String) = _uiState.update { it.copy(profileImageUrl = url) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            runCatching {
                profileRepository.updateProfile(
                    nickname = state.name.takeIf { it.isNotBlank() },
                    bio = state.bio.takeIf { it.isNotBlank() },
                    profileImage = state.profileImageUrl,
                )
                profileRepository.updateProfileSettings(
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
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
                onDone()
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
