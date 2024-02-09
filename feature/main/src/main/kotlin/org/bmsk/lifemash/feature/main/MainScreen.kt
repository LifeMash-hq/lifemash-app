package org.bmsk.lifemash.feature.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.launch
import org.bmsk.feature.topic.navigation.topicNavGraph
import java.net.UnknownHostException

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localContextResource = LocalContext.current.resources
    val onShowErrorSnackbar: (Throwable) -> Unit = { throwable ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                when (throwable) {
                    is UnknownHostException -> "네트워크 연결이 원할하지 않습니다"
                    else -> "알 수 없는 오류가 발생했습니다"
                },
            )
        }
    }

    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        topicNavGraph(
            onClickNews = { navigator.navigateWebView(it) },
        )
    }
}
