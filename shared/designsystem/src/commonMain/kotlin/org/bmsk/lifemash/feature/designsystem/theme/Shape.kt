package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * LifeMash corner radius tokens.
 *
 * Usage in Compose: `LifeMashShapes.card`, `LifeMashShapes.chip`, etc.
 * Raw dp values are also exposed for cases where a Shape is not directly needed.
 */
object LifeMashRadius {
    val xs = 8.dp // 작은 뱃지
    val sm = 12.dp // 썸네일 이미지
    val md = 16.dp // 카드, 이벤트 카드
    val lg = 20.dp // 큰 카드
    val xl = 26.dp // 검색바, 입력창
    val pill = 999.dp // 칩, 탭바 pill
}

/** Material3 Shapes mapped to LifeMash radius tokens. */
internal val LifeMashShapes = Shapes(
    extraSmall = RoundedCornerShape(LifeMashRadius.xs),
    small = RoundedCornerShape(LifeMashRadius.sm),
    medium = RoundedCornerShape(LifeMashRadius.md),
    large = RoundedCornerShape(LifeMashRadius.lg),
    extraLarge = RoundedCornerShape(LifeMashRadius.xl),
)
