package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing

/**
 * Represents a single tab in [LifeMashBottomTabBar].
 *
 * OCP: adding a new tab = adding an item to the list passed to [LifeMashBottomTabBar],
 * no changes to the component itself are needed.
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
)

/**
 * Pill-style bottom navigation bar for LifeMash.
 *
 * The active tab is highlighted with a slate-filled pill behind the icon.
 * Tab definitions are injected via [tabs] — this component never needs modification
 * when the tab list changes (OCP).
 */
@Composable
fun LifeMashBottomTabBar(
    tabs: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentSlate = MaterialTheme.colorScheme.primary // #475569
    val bgSurface = MaterialTheme.colorScheme.surface // #FFFFFF
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgSurface)
            .navigationBarsPadding()
            .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.xxs)
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val isSelected = tab.route == currentRoute

            val iconTint by animateColorAsState(
                targetValue = if (isSelected) Color.White else textSecondary,
                animationSpec = tween(durationMillis = 200),
                label = "iconTint-${tab.route}",
            )
            val pillBg by animateColorAsState(
                targetValue = if (isSelected) accentSlate else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "pillBg-${tab.route}",
            )
            val labelColor by animateColorAsState(
                targetValue = if (isSelected) accentSlate else textSecondary,
                animationSpec = tween(durationMillis = 200),
                label = "labelColor-${tab.route}",
            )

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(LifeMashRadius.pill))
                    .clickable { onItemClick(tab) }
                    .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xxs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(LifeMashRadius.pill))
                        .background(pillBg)
                        .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                )
            }
        }
    }
}
