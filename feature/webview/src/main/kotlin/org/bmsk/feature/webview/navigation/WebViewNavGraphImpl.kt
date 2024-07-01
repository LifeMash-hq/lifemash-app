package org.bmsk.feature.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.bmsk.feature.webview.WebViewRoute
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraph
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraphInfo
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