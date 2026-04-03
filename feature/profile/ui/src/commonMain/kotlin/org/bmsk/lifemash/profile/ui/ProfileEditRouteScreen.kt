package org.bmsk.lifemash.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.shared.common.rememberSingleImagePickerLauncher
import org.bmsk.lifemash.model.upload.UploadService
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileEditRouteScreen(
    onBack: () -> Unit,
    viewModel: ProfileEditViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uploadService: UploadService = koinInject()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val pickImage = rememberSingleImagePickerLauncher { media ->
        if (media != null) {
            scope.launch {
                runCatching {
                    val url = uploadService.upload(media.uri)
                    viewModel.updateProfileImage(url)
                }
            }
        }
    }

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
        onSave = { viewModel.save(onDone = onBack) },
        onCancel = onBack,
    )
}
