package org.bmsk.lifemash.onboarding.ui

import org.bmsk.lifemash.onboarding.domain.model.HandleValidationStatus

internal enum class OnboardingStep { PROFILE_SETUP, PERMISSIONS }

internal data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.PROFILE_SETUP,
    val name: String = "",
    val handle: String = "",
    val handleStatus: HandleValidationStatus = HandleValidationStatus.IDLE,
    val birthDate: String = "",
    val calendarConnected: Boolean = false,
    val notificationAllowed: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
) {
    val isProfileValid: Boolean
        get() = name.isNotBlank()
            && handle.length >= 3
            && handleStatus == HandleValidationStatus.AVAILABLE
}
