package org.bmsk.lifemash.feature.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import org.bmsk.lifemash.feature.scrap.api.ScrapNavController
import org.bmsk.lifemash.feature.scrap.api.ScrapNavGraph
import org.bmsk.lifemash.feature.topic.api.TopicNavController
import org.bmsk.lifemash.feature.topic.api.TopicNavGraph
import org.bmsk.lifemash.feature.topic.api.WebViewNavController
import org.bmsk.lifemash.feature.topic.api.WebViewNavGraph
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private var backPressedTime = 0L
    private val exitAppWhenBackButtonPressedTwiceCallback =
        createExitAppWhenBackButtonPressedTwiceCallback()

    @Inject
    lateinit var scrapNavGraph: ScrapNavGraph

    @Inject
    lateinit var scrapNavController: ScrapNavController

    @Inject
    lateinit var topicNavGraph: TopicNavGraph

    @Inject
    lateinit var topicNavController: TopicNavController

    @Inject
    lateinit var webViewNavGraph: WebViewNavGraph

    @Inject
    lateinit var webViewNavController: WebViewNavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(exitAppWhenBackButtonPressedTwiceCallback)

        setContent {
            LifeMashTheme {
                val navigator = rememberMainNavigator(
                    scrapNavController = scrapNavController,
                    topicNavController = topicNavController,
                    webViewNavController = webViewNavController,
                )

                MainScreen(
                    navigator = navigator,
                    scrapNavGraph = scrapNavGraph,
                    topicNavGraph = topicNavGraph,
                    webViewNavGraph = webViewNavGraph,
                )
            }
        }
    }

    private fun createExitAppWhenBackButtonPressedTwiceCallback() =
        object : OnBackPressedCallback(true) {
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
