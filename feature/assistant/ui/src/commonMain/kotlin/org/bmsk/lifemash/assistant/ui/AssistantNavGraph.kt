package org.bmsk.lifemash.assistant.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.bmsk.lifemash.assistant.api.ASSISTANT_ROUTE
import org.bmsk.lifemash.assistant.api.AssistantNavGraphInfo
import org.bmsk.lifemash.assistant.api.AssistantRoute
import org.bmsk.lifemash.feature.designsystem.component.BottomNavItem

val AssistantTab = BottomNavItem(
    route = ASSISTANT_ROUTE,
    icon = Icons.Outlined.AutoAwesome,
    selectedIcon = Icons.Filled.AutoAwesome,
    label = "AI",
)

fun NavGraphBuilder.assistantNavGraph(navInfo: AssistantNavGraphInfo) {
    composable<AssistantRoute> {
        AssistantRouteScreen(
            onShowErrorSnackbar = navInfo.onShowErrorSnackbar,
            onBack = navInfo.onBack,
        )
    }
}
