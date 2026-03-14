package org.bmsk.lifemash.main

import androidx.navigation.NavHostController
import org.bmsk.lifemash.feed.api.FeedNavController
import org.bmsk.lifemash.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.shared.webview.WebViewNavController
import org.bmsk.lifemash.feature.shared.webview.WebViewNavControllerInfo

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