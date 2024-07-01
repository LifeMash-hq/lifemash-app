package org.bmsk.lifemash.feature.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraphInfo
import org.bmsk.lifemash.feature.topic.api.TopicNavGraph
import org.bmsk.lifemash.feature.topic.api.TopicNavGraphInfo
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraph
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraphInfo
import java.net.UnknownHostException

@Composable
internal fun MainScreen(
    navigator: MainNavigator,
    scrapNavGraph: ScrapNavGraph,
    topicNavGraph: TopicNavGraph,
    webViewNavGraph: WebViewNavGraph,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localContextResource = LocalContext.current.resources
    val onShowErrorSnackbar: (Throwable?) -> Unit = { throwable ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                when (throwable) {
                    is UnknownHostException -> localContextResource.getString(R.string.the_network_connection_is_not_smooth)
                    else -> localContextResource.getString(R.string.unknown_error_occurred)
                },
            )
        }
    }

    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        topicNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = TopicNavGraphInfo(
                onClickNews = { navigator.navigateWebView(it) },
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        )

        scrapNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = ScrapNavGraphInfo(onShowErrorSnackbar)
        )

        webViewNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = WebViewNavGraphInfo(onShowErrorSnackbar),
        )
    }
}
