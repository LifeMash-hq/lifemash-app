package org.bmsk.lifemash.onboarding.impl

import org.bmsk.lifemash.domain.onboarding.HandleValidationStatus

internal enum class OnboardingStep { PROFILE_SETUP, PERMISSIONS }

internal data class OnboardingUiState(
    val step: OnboardingStep,
    val name: String,
    val handle: String,
    val handleStatus: HandleValidationStatus,
    val birthDate: String,
    val calendarConnected: Boolean,
    val notificationAllowed: Boolean,
    val isSaving: Boolean,
    val error: String?,
) {
    val isProfileValid: Boolean
        get() = name.isNotBlank()
            && handle.length >= 3
            && handleStatus == HandleValidationStatus.AVAILABLE

    companion object {
        val Default = OnboardingUiState(
            step = OnboardingStep.PROFILE_SETUP,
            name = "",
            handle = "",
            handleStatus = HandleValidationStatus.IDLE,
            birthDate = "",
            calendarConnected = false,
            notificationAllowed = false,
            isSaving = false,
            error = null,
        )
    }
}
