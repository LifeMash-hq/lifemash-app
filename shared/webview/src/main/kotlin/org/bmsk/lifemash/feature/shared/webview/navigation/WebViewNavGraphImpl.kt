package org.bmsk.lifemash.feature.shared.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.bmsk.lifemash.feature.shared.webview.WebViewRoute
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraph
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraphInfo
import javax.inject.Inject

class WebViewNavGraphImpl @Inject constructor() : WebViewNavGraph {

    override fun buildNavGraph(navGraphBuilder: NavGraphBuilder, navInfo: WebViewNavGraphInfo) {
        navGraphBuilder.composable(
            route = WebViewRoute.createWebViewRoute("{url}"),
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                },
            ),
        ) { navBackStackEntry ->
            val url = navBackStackEntry.arguments?.getString("url") ?: ""
            WebViewRoute(url = url)
        }
    }
}