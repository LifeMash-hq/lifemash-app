package org.bmsk.feature.webview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun WebViewScreen(
    url: String,
) {
    AndroidView(
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
