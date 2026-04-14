package org.bmsk.lifemash.profile.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.feature.shared.common.rememberSingleImagePickerLauncher
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

    LaunchedEffect(uiState.event) {
        when (uiState.event) {
            is ProfileEditEvent.Saved -> onBack()
            null -> return@LaunchedEffect
        }
        viewModel.consumeEvent()
    }

    val pickImage = rememberSingleImagePickerLauncher { media ->
        if (media != null) {
            viewModel.uploadAndUpdateImage(media.uri)
        }
    }

    if (!uiState.isLoaded) return

    ProfileEditScreen(
        uiState = uiState,
        onPickImage = { pickImage() },
        onNameChange = viewModel::updateName,
        onUsernameChange = viewModel::updateUsername,
        onBioChange = viewModel::updateBio,
        onDefaultSubTabChange = viewModel::updateDefaultSubTab,
        onMyCalendarViewChange = viewModel::updateMyCalendarView,
        onOthersCalendarViewChange = viewModel::updateOthersCalendarView,
        onDefaultVisibilityChange = viewModel::updateDefaultVisibility,
        onSave = viewModel::save,
        onCancel = onBack,
    )
}
