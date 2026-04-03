package org.bmsk.lifemash.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingRouteScreen(
    onOnboardingComplete: () -> Unit,
    onShowErrorSnackbar: (Throwable?) -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launchCalendar = rememberCalendarPermissionLauncher { granted ->
        viewModel.updateCalendarConnected(granted)
    }
    val launchNotification = rememberNotificationPermissionLauncher { granted ->
        viewModel.updateNotificationAllowed(granted)
    }

    when (uiState.step) {
        OnboardingStep.PROFILE_SETUP -> ProfileSetupScreen(
            uiState = uiState,
            onBackClick = onOnboardingComplete,
            onNameChange = viewModel::updateName,
            onHandleChange = viewModel::updateHandle,
            onBirthDateChange = viewModel::updateBirthDate,
            onNextClick = {
                viewModel.saveProfile(
                    onComplete = {}
                )
            },
        )
        OnboardingStep.PERMISSIONS -> PermissionScreen(
            uiState = uiState,
            onBackClick = { viewModel.stepBack() },
            onCalendarConnect = launchCalendar,
            onNotificationAllow = launchNotification,
            onStartClick = onOnboardingComplete,
            onSkipClick = onOnboardingComplete,
        )
    }
}
