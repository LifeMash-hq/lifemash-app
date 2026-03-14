package org.bmsk.lifemash.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.feed.api.FeedNavController
import org.bmsk.lifemash.feed.api.FeedNavGraph
import org.bmsk.lifemash.feed.api.FeedNavGraphInfo
import org.bmsk.lifemash.scrap.api.ScrapNavController
import org.bmsk.lifemash.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.scrap.api.ScrapNavGraphInfo
import org.bmsk.lifemash.feature.shared.webview.WebViewNavController
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraph
import org.bmsk.lifemash.feature.shared.webview.WebViewNavGraphInfo
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.system.exitProcess

internal class MainNavigationDependencies @Inject constructor(
    // NavGraphs
    val scrapNavGraph: ScrapNavGraph,
    val webViewNavGraph: WebViewNavGraph,
    val feedNavGraph: FeedNavGraph,
    // NavControllers
    val scrapNavController: ScrapNavController,
    val webViewNavController: WebViewNavController,
    val feedNavController: FeedNavController,
)

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mainNavDep: MainNavigationDependencies
    private var backPressedTime = 0L
    private val exitWhenBackButtonPressedTwiceCall = getOnBackPressedCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(exitWhenBackButtonPressedTwiceCall)

        setContent {
            LifeMashTheme {
                val navController = rememberNavController()
                val mainNavigator = remember(navController) {
                    MainNavigator(
                        navController = navController,
                        scrapNavController = mainNavDep.scrapNavController,
                        webViewNavController = mainNavDep.webViewNavController,
                        feedNavController = mainNavDep.feedNavController,
                    )
                }

                MainScreen(
                    navController = navController,
                    mainNavigator = mainNavigator,
                    scrapNavGraph = mainNavDep.scrapNavGraph,
                    webViewNavGraph = mainNavDep.webViewNavGraph,
                    feedNavGraph = mainNavDep.feedNavGraph,
                )
            }
        }
    }

    private fun getOnBackPressedCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (backButtonPressedTwiceWithinFinishInterval()) {
                exitApp()
            } else {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.guide_double_tab_exit),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        private fun backButtonPressedTwiceWithinFinishInterval(): Boolean {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastBackButtonPress = currentTime - backPressedTime
            return timeSinceLastBackButtonPress in 0..FINISH_INTERVAL_TIME
        }

        private fun exitApp() {
            ActivityCompat.finishAffinity(this@MainActivity)
            exitProcess(0)
        }
    }

    companion object {
        private const val FINISH_INTERVAL_TIME = 2000L
    }
}

@Composable
internal fun MainScreen(
    navController: NavHostController,
    mainNavigator: MainNavigator,
    scrapNavGraph: ScrapNavGraph,
    webViewNavGraph: WebViewNavGraph,
    feedNavGraph: FeedNavGraph,
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
        navController = navController,
        startDestination = mainNavigator.startDestination,
    ) {
        scrapNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = ScrapNavGraphInfo(
                onClickNews = { mainNavigator.navigateWebView(it) },
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        )

        webViewNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = WebViewNavGraphInfo(onShowErrorSnackbar),
        )

        feedNavGraph.buildNavGraph(
            navGraphBuilder = this,
            navInfo = FeedNavGraphInfo {
                mainNavigator.navigateWebView(it)
            },
        )
    }
}