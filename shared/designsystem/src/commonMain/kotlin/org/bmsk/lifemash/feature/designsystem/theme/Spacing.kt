package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * LifeMash spacing scale — tokens.css 기준: 2 / 4 / 6 / 8 / 12 / 16 / 20 / 24 / 32 / 40 dp
 *
 * For responsive screen padding use [screenPaddingForWidth]:
 *   Compact (<600dp) → 16dp / Medium (600–840dp) → 24dp / Expanded (≥840dp) → 32dp
 */
object LifeMashSpacing {
    val micro: Dp = 2.dp
    val xxs: Dp = 4.dp
    val xs: Dp = 6.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 40.dp

    fun screenPaddingForWidth(widthDp: Int): Dp = when {
        widthDp < 600 -> lg
        widthDp < 840 -> xxl
        else -> xxxl
    }
}
