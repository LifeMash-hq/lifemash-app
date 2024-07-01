package org.bmsk.feature.webview.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import org.bmsk.feature.webview.WebViewRoute
import org.bmsk.lifemash.feature.topic.api.WebViewNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavControllerInfo
import javax.inject.Inject

class WebViewNavControllerImpl @Inject constructor() : WebViewNavController {
    override fun route(): String {
        return WebViewRoute.ROUTE
    }

    override fun navigate(navController: NavController, navInfo: WebViewNavControllerInfo) {
        navController.navigate(
            WebViewRoute.createWebViewRoute(navInfo.url),
            navOptions {
                restoreState = true
            },
        )
    }
}