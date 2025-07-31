@file:Suppress("MaximumLineLength", "MaxLineLength")

package org.bmsk.lifemash.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.core.designsystem.R
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    val primaryColor = Color(0xFF2D99FF)
    val secondaryColor = Color(0xFFE6F3FF)
    val delays = listOf(0, 150, 300)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val alphaAnim = rememberInfiniteTransition(label = "alphaAnim")
        val alpha by alphaAnim.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Text(
            text = stringResource(R.string.core_designsystem_app_name),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = primaryColor.copy(alpha = alpha),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.core_designsystem_loading),
            fontSize = 15.sp,
            color = secondaryColor,
        )

        Spacer(Modifier.height(20.dp))

        Row {
            for (i in 0..2) {
                val anim = rememberInfiniteTransition(label = "dotAnim")
                val offsetY by anim.animateFloat(
                    initialValue = 0f,
                    targetValue = -18f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700, easing = EaseInOutCubic, delayMillis = delays[i]),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "offsetY"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(18.dp)
                        .offset(y = offsetY.dp)
                        .background(
                            color = primaryColor,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun LifeMashSplashPreview() {
    LifeMashTheme {
        Loading()
    }
}