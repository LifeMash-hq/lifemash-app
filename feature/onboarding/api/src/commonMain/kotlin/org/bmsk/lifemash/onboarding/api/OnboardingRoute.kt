package org.bmsk.lifemash.onboarding.api

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

data class OnboardingNavGraphInfo(
    val onOnboardingComplete: () -> Unit,
    val onShowErrorSnackbar: (Throwable?) -> Unit,
)
