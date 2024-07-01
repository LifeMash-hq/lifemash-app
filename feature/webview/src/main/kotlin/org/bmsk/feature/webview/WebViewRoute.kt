package org.bmsk.feature.webview

import androidx.compose.runtime.Composable

internal object WebViewRoute {
    const val ROUTE = "webview"

    fun createWebViewRoute(url: String): String = "$ROUTE?url=$url"

    @Composable
    operator fun invoke(
        url: String,
    ) {
        WebViewScreen(url = url)
    }
}

