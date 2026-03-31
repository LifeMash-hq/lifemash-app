package org.bmsk.lifemash.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * LifeMash corner radius tokens — tokens.css 기준.
 *
 * sm=6dp, md=10dp, lg=16dp, xl=20dp, full=9999dp (CircleShape)
 */
object LifeMashRadius {
    val sm = 6.dp
    val md = 10.dp
    val lg = 16.dp
    val xl = 20.dp
    val full = 9999.dp
}

/** Material3 Shapes mapped to LifeMash radius tokens. */
internal val LifeMashShapes = Shapes(
    extraSmall = RoundedCornerShape(LifeMashRadius.sm),
    small = RoundedCornerShape(LifeMashRadius.md),
    medium = RoundedCornerShape(LifeMashRadius.lg),
    large = RoundedCornerShape(LifeMashRadius.xl),
    extraLarge = RoundedCornerShape(LifeMashRadius.full),
)
