package org.bmsk.lifemash.feature.topic.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.bmsk.lifemash.feature.topic.TopicRoute
import org.bmsk.lifemash.feature.topic.WebViewRoute

fun NavController.navigateTopic() {
    navigate(TopicRoute.route)
}

fun NavController.navigateWebView(url: String) {
    navigate(
        TopicRoute.createWebViewRoute(url),
        navOptions {
            restoreState = true
        },
    )
}

fun NavGraphBuilder.topicNavGraph(
    onClickNews: (url: String) -> Unit,
    onShowErrorSnackbar: (throwable: Throwable) -> Unit,
) {
    composable(route = TopicRoute.route) {
        TopicRoute(
            onClickNews = onClickNews,
            onShowErrorSnackbar = onShowErrorSnackbar,
        )
    }
    composable(
        route = TopicRoute.createWebViewRoute("{url}"),
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

object TopicRoute {
    const val route: String = "topic"
    fun createWebViewRoute(url: String): String = "webview?url=$url"
}
