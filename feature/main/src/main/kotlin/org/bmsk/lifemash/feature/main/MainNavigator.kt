package org.bmsk.lifemash.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.bmsk.lifemash.feature.feed.api.FeedNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.topic.api.TopicNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavControllerInfo

internal class MainNavigator(
    private val scrapNavController: ScrapNavController,
    private val topicNavController: TopicNavController,
    private val webViewNavController: WebViewNavController,
    private val feedNavController: FeedNavController,
    val navController: NavHostController,
) {
//    private val currentDestination: NavDestination?
//        @Composable get() = navController
//            .currentBackStackEntryAsState().value?.destination

    val startDestination = feedNavController.route()

    fun navigateWebView(url: String) {
        webViewNavController.navigate(navController, WebViewNavControllerInfo(url))
    }

    fun navigateScrap() {
        scrapNavController.navigate(navController, Unit)
    }

    fun navigateTopic() {
        topicNavController.navigate(navController, Unit)
    }

    fun navigateFeed() {
        feedNavController.navigate(navController, Unit)
    }
}

@Composable
internal fun rememberMainNavigator(
    scrapNavController: ScrapNavController,
    topicNavController: TopicNavController,
    webViewNavController: WebViewNavController,
    feedNavController: FeedNavController,
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(
        scrapNavController = scrapNavController,
        topicNavController = topicNavController,
        webViewNavController = webViewNavController,
        feedNavController = feedNavController,
        navController = navController
    )
}
