package org.bmsk.lifemash.feature.topic.api

import org.bmsk.lifemash.feature.nav.LifeMashNavController

interface WebViewNavController : LifeMashNavController<WebViewNavControllerInfo>

data class WebViewNavControllerInfo(
    val url: String,
)
