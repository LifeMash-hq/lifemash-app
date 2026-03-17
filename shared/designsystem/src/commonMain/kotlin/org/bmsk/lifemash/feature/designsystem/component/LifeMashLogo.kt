package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.AccentCoral
import org.bmsk.lifemash.feature.designsystem.theme.TextPrimary

@Composable
fun LifeMashLogo(modifier: Modifier = Modifier, size: Dp = 88.dp) {
    val scale = size / 88.dp

    Box(modifier = modifier.size(size)) {
        // 좌상 위성 도트
        Box(
            modifier = Modifier
                .offset(x = 8.dp * scale, y = 8.dp * scale)
                .size(16.dp * scale)
                .alpha(0.25f)
                .clip(CircleShape)
                .background(TextPrimary),
        )
        // 우상 위성 도트
        Box(
            modifier = Modifier
                .offset(x = 62.dp * scale, y = 10.dp * scale)
                .size(16.dp * scale)
                .alpha(0.20f)
                .clip(CircleShape)
                .background(TextPrimary),
        )
        // 좌하 위성 도트
        Box(
            modifier = Modifier
                .offset(x = 12.dp * scale, y = 60.dp * scale)
                .size(16.dp * scale)
                .alpha(0.22f)
                .clip(CircleShape)
                .background(TextPrimary),
        )
        // 중앙 coral 원
        Box(
            modifier = Modifier
                .offset(x = 30.dp * scale, y = 30.dp * scale)
                .size(28.dp * scale)
                .clip(CircleShape)
                .background(AccentCoral),
        )
    }
}
