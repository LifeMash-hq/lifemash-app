package org.bmsk.lifemash.feature.shared.webview.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import org.bmsk.lifemash.feature.shared.webview.WebViewRoute
import org.bmsk.lifemash.feature.shared.webview.WebViewNavController
import org.bmsk.lifemash.feature.shared.webview.WebViewNavControllerInfo
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