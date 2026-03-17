package org.bmsk.lifemash.feature.shared.webview

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private fun isAllowedUrl(url: String): Boolean {
    val scheme = Uri.parse(url).scheme?.lowercase()
    return scheme == "https" || scheme == "http"
}

@Composable
actual fun PlatformWebView(url: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val requestUrl = request?.url?.toString() ?: return true
                        return !isAllowedUrl(requestUrl)
                    }
                }
                with(settings) {
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                }
            }
        },
        update = { webView ->
            if (isAllowedUrl(url)) {
                webView.loadUrl(url)
            }
        },
    )
}
