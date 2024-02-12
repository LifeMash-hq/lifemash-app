package org.bmsk.lifemash.feature.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import kotlin.system.exitProcess

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private var backPressedTime = 0L
    private val exitAppWhenBackButtonPressedTwiceCallback =
        createExitAppWhenBackButtonPressedTwiceCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(exitAppWhenBackButtonPressedTwiceCallback)

        setContent {
            LifeMashTheme {
                MainScreen()
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
