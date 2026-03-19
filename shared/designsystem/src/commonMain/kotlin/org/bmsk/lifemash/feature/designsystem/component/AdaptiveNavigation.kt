package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashSpacing

private const val EXPANDED_BREAKPOINT_DP = 840

@Composable
fun AdaptiveNavigation(
    tabs: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val isExpanded = maxWidth >= EXPANDED_BREAKPOINT_DP.dp

        if (isExpanded) {
            ExpandedLayout(tabs, currentRoute, onItemClick, Modifier, content)
        } else {
            CompactLayout(tabs, currentRoute, onItemClick, Modifier, content)
        }
    }
}

@Composable
private fun CompactLayout(
    tabs: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            content()
        }
        LifeMashBottomTabBar(
            tabs = tabs,
            currentRoute = currentRoute,
            onItemClick = onItemClick,
        )
    }
}

@Composable
private fun ExpandedLayout(
    tabs: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val accentSlate = MaterialTheme.colorScheme.primary
    val bgSurface = MaterialTheme.colorScheme.surface

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.background(bgSurface).navigationBarsPadding().width(80.dp),
            containerColor = bgSurface,
        ) {
            tabs.forEach { tab ->
                val isSelected = tab.route == currentRoute
                NavigationRailItem(
                    selected = isSelected,
                    onClick = { onItemClick(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    label = { Text(text = tab.label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = accentSlate,
                        indicatorColor = accentSlate,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    alwaysShowLabel = true,
                )
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(start = LifeMashSpacing.lg)) {
            content()
        }
    }
}
