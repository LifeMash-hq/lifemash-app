package org.bmsk.lifemash.designsystem.component

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
import org.bmsk.lifemash.designsystem.theme.LifeMashGradient

enum class AvatarSize(val dp: Dp) {
    Small(28.dp),
    Medium(40.dp),
    Large(56.dp),
    XLarge(72.dp),
    XXLarge(80.dp),
}

@Composable
fun LifeMashAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    name: String = "",
    size: AvatarSize = AvatarSize.Medium,
) {
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
        val gradient = LifeMashGradient.primaryBrush()
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(gradient),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = when (size) {
                    AvatarSize.Small -> MaterialTheme.typography.labelSmall
                    AvatarSize.Medium -> MaterialTheme.typography.titleSmall
                    AvatarSize.Large -> MaterialTheme.typography.titleLarge
                    AvatarSize.XLarge -> MaterialTheme.typography.displaySmall
                    AvatarSize.XXLarge -> MaterialTheme.typography.displayMedium
                },
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
