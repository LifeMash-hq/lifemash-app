package org.bmsk.lifemash.moment.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.domain.moment.Visibility

// Icons imported from designsystem icon set
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Public

@Composable
internal fun PrivacyToggleButton(
    visibility: Visibility,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, label) = when (visibility) {
        Visibility.PUBLIC -> Icons.Rounded.Public to "전체 공개"
        Visibility.FOLLOWERS -> Icons.Rounded.People to "팔로워만"
        Visibility.PRIVATE -> Icons.Rounded.Lock to "비공개"
    }

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = LifeMashSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(LifeMashSpacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
