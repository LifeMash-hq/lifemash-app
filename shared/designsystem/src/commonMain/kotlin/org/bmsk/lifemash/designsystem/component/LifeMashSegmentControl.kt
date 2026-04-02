package org.bmsk.lifemash.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashShadow
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

@Composable
fun LifeMashSegmentControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = true, // 모든 아이템 너비를 동일하게 할지 여부
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center, // 정렬 방식 제어
) {
    val semantic = LocalLifeMashColors.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(LifeMashRadius.md))
            .background(semantic.chipBg)
            .padding(3.dp)
            .height(IntrinsicSize.Min), // 아이템들 중 가장 큰 높이에 맞춤
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex

            // 각 아이템의 가중치 또는 너비 결정
            val itemModifier = if (equalWidth) {
                Modifier.weight(1f)
            } else {
                Modifier
            }

            SegmentItem(
                label = label,
                isSelected = isSelected,
                onClick = { onSelect(index) },
                modifier = itemModifier
            )
        }
    }
}

@Composable
private fun SegmentItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Surface(
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(7.dp),
            shadowElevation = LifeMashShadow.sm,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.xs),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(7.dp))
                .clickable { onClick() }
                .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.xs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}