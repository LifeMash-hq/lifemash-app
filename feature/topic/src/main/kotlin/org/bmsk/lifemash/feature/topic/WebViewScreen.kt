package org.bmsk.lifemash.feature.topic

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun WebViewRoute(
    url: String,
) {
    WebViewScreen(url = url)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewScreen(
    url: String,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                with(settings) {
                    javaScriptEnabled = true
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
