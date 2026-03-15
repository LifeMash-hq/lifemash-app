package org.bmsk.lifemash.feature.shared.webview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun PlatformWebView(url: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                with(settings) {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                }
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
    )
}
