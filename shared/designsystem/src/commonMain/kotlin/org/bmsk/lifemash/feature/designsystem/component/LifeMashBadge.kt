package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.feature.designsystem.theme.LocalLifeMashColors

enum class LifeMashBadgeStyle { Danger, Primary, Success }

@Composable
fun LifeMashBadge(
    count: Int,
    modifier: Modifier = Modifier,
    style: LifeMashBadgeStyle = LifeMashBadgeStyle.Danger,
) {
    val bg = when (style) {
        LifeMashBadgeStyle.Danger -> LocalLifeMashColors.current.danger
        LifeMashBadgeStyle.Primary -> MaterialTheme.colorScheme.primary
        LifeMashBadgeStyle.Success -> LocalLifeMashColors.current.success
    }
    val label = if (count > 99) "99+" else count.toString()

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            .clip(RoundedCornerShape(LifeMashRadius.full))
            .background(bg)
            .padding(horizontal = LifeMashSpacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
