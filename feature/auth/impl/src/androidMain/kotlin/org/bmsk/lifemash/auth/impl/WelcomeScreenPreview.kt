package org.bmsk.lifemash.auth.impl

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.component.LifeMashBackground
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

@Preview(name = "Light", showSystemUi = true)
@Preview(name = "Dark", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun WelcomeScreenPreview() {
    LifeMashTheme {
        LifeMashBackground {
            WelcomeScreen(
                onStartClick = {},
                onLoginClick = {},
            )
        }
    }
}
