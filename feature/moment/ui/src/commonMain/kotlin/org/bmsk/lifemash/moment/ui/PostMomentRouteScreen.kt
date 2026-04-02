package org.bmsk.lifemash.moment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostMomentRouteScreen(
    onSuccess: () -> Unit,
    onClose: () -> Unit,
) {
    val viewModel: PostMomentViewModel = koinViewModel()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is PostMomentUiState.Success) onSuccess()
    }

    PostMomentScreen(
        form = form,
        uiState = uiState,
        onCaptionChange = viewModel::onCaptionChange,
        onCycleVisibility = viewModel::onCycleVisibility,
        onTagEventClick = { /* TODO: 일정 선택 바텀시트 */ },
        onAddMedia = { /* TODO: 미디어 피커 (platform expect/actual) */ },
        onRemoveMedia = viewModel::onRemoveMedia,
        onSubmit = {
            viewModel.onSubmit { media ->
                // TODO: presigned URL → S3 직접 업로드 구현 (platform-specific)
                // 현재는 localUri를 그대로 반환 (개발 테스트용)
                media.localUri
            }
        },
        onClose = onClose,
    )
}
