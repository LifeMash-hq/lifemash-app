package org.bmsk.lifemash.profile.impl

data class ProfileEditUiState(
    val name: String,
    val username: String,
    val bio: String,
    val profileImageUrl: String?,
    val defaultSubTab: Int,
    val myCalendarView: Int,
    val othersCalendarView: Int,
    val defaultVisibility: Int,
    val isSaving: Boolean,
    val error: String?,
    val event: ProfileEditEvent?,
) {
    companion object {
        val Default = ProfileEditUiState(
            name = "",
            username = "",
            bio = "",
            profileImageUrl = null,
            defaultSubTab = 0,
            myCalendarView = 0,
            othersCalendarView = 0,
            defaultVisibility = 0,
            isSaving = false,
            error = null,
            event = null,
        )
    }
}

sealed interface ProfileEditEvent {
    data object Saved : ProfileEditEvent
}
