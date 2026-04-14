package org.bmsk.lifemash.onboarding.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.onboarding.api.OnboardingNavGraphInfo
import org.bmsk.lifemash.onboarding.api.OnboardingRoute

fun NavGraphBuilder.onboardingNavGraph(navInfo: OnboardingNavGraphInfo) {
    composable<OnboardingRoute> {
        OnboardingRouteScreen(
            onOnboardingComplete = navInfo.onOnboardingComplete,
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
        )
    }
}
