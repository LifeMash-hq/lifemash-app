package org.bmsk.lifemash.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * tokens.css shadow tokens mapped to Compose elevation (Dp).
 *
 * CSS box-shadow cannot be exactly replicated in Compose;
 * the Dp values below are reasonable approximations used with `shadowElevation`.
 */
object LifeMashShadow {
    /** --shadow-sm: 0 1px 4px rgba(0,0,0,0.06) */
    val sm: Dp = 2.dp

    /** --shadow-md: 0 2px 10px rgba(0,0,0,0.07) */
    val md: Dp = 6.dp

    /** --shadow-lg: 0 20px 60px rgba(0,0,0,0.12) */
    val lg: Dp = 16.dp
}
