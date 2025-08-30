package org.bmsk.lifemash.feature.main

import androidx.navigation.NavHostController
import org.bmsk.lifemash.feature.feed.api.FeedNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavControllerInfo

internal class MainNavigator(
    private val navController: NavHostController,
    private val scrapNavController: ScrapNavController,
    private val webViewNavController: WebViewNavController,
    private val feedNavController: FeedNavController,
) {
    val startDestination = feedNavController.route()

    fun navigateWebView(url: String) {
        webViewNavController.navigate(navController, WebViewNavControllerInfo(url))
    }

    fun navigateScrap() {
        scrapNavController.navigate(navController, Unit)
    }

    fun navigateFeed() {
        feedNavController.navigate(navController, Unit)
    }
}