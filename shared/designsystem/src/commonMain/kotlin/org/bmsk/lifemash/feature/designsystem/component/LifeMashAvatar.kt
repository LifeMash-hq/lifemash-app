package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LocalLifeMashColors

enum class AvatarSize(val dp: Dp) {
    Small(28.dp),
    Medium(40.dp),
    Large(56.dp),
}

@Composable
fun LifeMashAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    name: String = "",
    size: AvatarSize = AvatarSize.Medium,
) {
    val semantic = LocalLifeMashColors.current
    val initial = name.firstOrNull()?.toString().orEmpty()

    if (imageUrl != null) {
        NetworkImage(
            imageUrl = imageUrl,
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(semantic.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = when (size) {
                    AvatarSize.Small -> MaterialTheme.typography.labelSmall
                    AvatarSize.Medium -> MaterialTheme.typography.titleSmall
                    AvatarSize.Large -> MaterialTheme.typography.titleLarge
                },
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
