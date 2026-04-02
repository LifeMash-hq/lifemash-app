package org.bmsk.lifemash.moment.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.NetworkImage
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.ui.SelectedMedia

private val ThumbSize = 80.dp

@Composable
internal fun MediaThumbnailRow(
    media: List<SelectedMedia>,
    isMediaFull: Boolean,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
        contentPadding = PaddingValues(horizontal = LifeMashSpacing.lg),
    ) {
        items(media, key = { it.id }) { item ->
            MediaThumb(item = item, onRemove = { onRemove(item.id) })
        }
        if (!isMediaFull) {
            item {
                AddThumb(onClick = onAdd)
            }
        }
    }
}

@Composable
private fun MediaThumb(
    item: SelectedMedia,
    onRemove: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(ThumbSize)
            .clip(RoundedCornerShape(LifeMashRadius.md)),
    ) {
        NetworkImage(
            imageUrl = item.localUri,
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
        )

        // 동영상: 재생 아이콘 + 시간 배지
        if (item.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    .padding(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            item.durationMs?.let { ms ->
                Text(
                    text = formatDuration(ms),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }

        // 삭제 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "삭제",
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun AddThumb(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(ThumbSize)
            .clip(RoundedCornerShape(LifeMashRadius.md))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "추가",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "추가",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "$min:${sec.toString().padStart(2, '0')}"
}
