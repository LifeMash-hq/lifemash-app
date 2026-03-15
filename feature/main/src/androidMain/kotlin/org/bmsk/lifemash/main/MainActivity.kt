package org.bmsk.lifemash.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.launch
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTheme
import java.net.UnknownHostException
import kotlin.system.exitProcess

internal class MainActivity : AppCompatActivity() {
    private var backPressedTime = 0L
    private val exitWhenBackButtonPressedTwiceCall = getOnBackPressedCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(exitWhenBackButtonPressedTwiceCall)

        setContent {
            LifeMashTheme {
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

                MainScreen(onShowErrorSnackbar = onShowErrorSnackbar)
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
