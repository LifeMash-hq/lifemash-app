package org.bmsk.lifemash.moment.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.moment.impl.component.EventTagChip
import org.bmsk.lifemash.moment.impl.component.MediaThumbnailRow
import org.bmsk.lifemash.moment.impl.component.PrivacyToggleButton

@Composable
internal fun PostMomentScreen(
    form: PostMomentFormState,
    uiState: PostMomentUiState,
    onCaptionChange: (String) -> Unit,
    onCycleVisibility: () -> Unit,
    onTagEventClick: () -> Unit,
    onAddMedia: () -> Unit,
    onRemoveMedia: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUploading = uiState is PostMomentUiState.Uploading

    Column(modifier = modifier) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onClose, enabled = !isUploading) {
                Text("✕", style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = "올리기",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(horizontal = LifeMashSpacing.sm),
            )
            LifeMashButton(
                text = if (isUploading) "올리는 중…" else "공유하기",
                onClick = onSubmit,
                enabled = form.canSubmit && !isUploading,
            )
        }

        HorizontalDivider()

        Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.lg)) {
            Spacer(Modifier.height(LifeMashSpacing.md))

            EventTagChip(
                eventTitle = form.eventTitle,
                onChangeClick = onTagEventClick,
            )

            LifeMashInput(
                value = form.caption,
                onValueChange = onCaptionChange,
                placeholder = "내용을 입력하세요. (선택사항)",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                maxLines = 6,
                isError = form.caption.length > 200,
                errorMessage = if (form.caption.length > 200) "${form.caption.length}/200" else null,
            )

            Spacer(Modifier.height(LifeMashSpacing.md))
            Text(
                text = "사진 · 영상",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(LifeMashSpacing.sm))

        MediaThumbnailRow(
            media = form.media,
            isMediaFull = form.isMediaFull,
            onRemove = onRemoveMedia,
            onAdd = onAddMedia,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(LifeMashSpacing.md))
        HorizontalDivider()

        PrivacyToggleButton(
            visibility = form.visibility,
            onClick = onCycleVisibility,
            modifier = Modifier.padding(horizontal = LifeMashSpacing.lg),
        )
    }
}
