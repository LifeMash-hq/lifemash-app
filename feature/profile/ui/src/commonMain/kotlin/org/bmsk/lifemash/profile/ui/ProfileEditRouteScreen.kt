package org.bmsk.lifemash.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileEditRouteScreen(
    onBack: () -> Unit,
    viewModel: ProfileEditViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ProfileEditScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onUsernameChange = viewModel::updateUsername,
        onBioChange = viewModel::updateBio,
        onDefaultSubTabChange = viewModel::updateDefaultSubTab,
        onMyCalendarViewChange = viewModel::updateMyCalendarView,
        onOthersCalendarViewChange = viewModel::updateOthersCalendarView,
        onDefaultVisibilityChange = viewModel::updateDefaultVisibility,
        onSave = { viewModel.save(onDone = onBack) },
        onCancel = onBack,
    )
}
