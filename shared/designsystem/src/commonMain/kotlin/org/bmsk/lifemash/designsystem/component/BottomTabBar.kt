package org.bmsk.lifemash.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LocalLifeMashColors

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
    val badgeCount: Int = 0,
)

/**
 * Bottom navigation bar for LifeMash.
 *
 * The active tab is highlighted with the primary color.
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
    val primary = MaterialTheme.colorScheme.primary
    val navBg = LocalLifeMashColors.current.navBg
    val navBorder = LocalLifeMashColors.current.navBorder
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = 1.dp, color = navBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(navBg)
                .navigationBarsPadding()
                .padding(horizontal = LifeMashSpacing.lg, vertical = LifeMashSpacing.xxs)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                val isSelected = tab.route == currentRoute

                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) primary else textSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "tint-${tab.route}",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(LifeMashRadius.full))
                        .clickable { onItemClick(tab) }
                        .padding(vertical = LifeMashSpacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label,
                        tint = tintColor,
                        modifier = Modifier.size(22.dp),
                    )
                    if (tab.badgeCount > 0) {
                        LifeMashBadge(
                            count = tab.badgeCount,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp),
                        )
                    }
                }
            }
        }
    }
}
