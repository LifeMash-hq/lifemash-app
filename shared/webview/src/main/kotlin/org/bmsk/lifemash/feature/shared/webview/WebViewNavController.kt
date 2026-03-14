package org.bmsk.lifemash.feature.shared.webview

import org.bmsk.lifemash.feature.shared.navigation.LifeMashNavController

interface WebViewNavController : LifeMashNavController<WebViewNavControllerInfo>

data class WebViewNavControllerInfo(
    val url: String,
)
