package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

enum class PrivacyLevel { Public, Friend, Private }

@Composable
fun LifeMashPrivacyLabel(
    level: PrivacyLevel,
    modifier: Modifier = Modifier,
) {
    val semantic = LocalLifeMashColors.current

    val (
        bgColor,
        textColor,
        label,
    ) = when (level) {
        PrivacyLevel.Public -> Triple(
            Color(0x1F00B894),
            Color(0xFF00B894),
            "공개",
        )
        PrivacyLevel.Friend -> Triple(
            Color(0x1FE17055),
            Color(0xFFE17055),
            "친구",
        )
        PrivacyLevel.Private -> Triple(
            semantic.primaryLight,
            MaterialTheme.colorScheme.primary,
            "비공개",
        )
    }

    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = LifeMashSpacing.sm, vertical = LifeMashSpacing.micro),
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
    )
}
