package org.bmsk.lifemash.feature.shared.webview

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.webViewNavGraph(navInfo: WebViewNavGraphInfo) {
    composable<WebViewRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WebViewRoute>()
        PlatformWebView(url = route.url)
    }
}
