package org.bmsk.lifemash.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object LifeMashIcons {

    /**
     * 두 사람 아이콘 (Lucide Users 스타일).
     * Design SVG: viewBox="0 0 24 24", stroke 1.5, fill none.
     */
    val People: ImageVector by lazy {
        ImageVector.Builder(
            name = "People",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path: M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17f, 21f)
                verticalLineToRelative(-2f)
                arcToRelative(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -4f,
                    -4f,
                )
                horizontalLineTo(5f)
                arcToRelative(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -4f,
                    4f,
                )
                verticalLineToRelative(2f)
            }
            // circle cx="9" cy="7" r="4" — two semicircles
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(13f, 7f)
                arcTo(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    5f,
                    7f,
                )
                arcTo(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    13f,
                    7f,
                )
                close()
            }
            // path: M23 21v-2a4 4 0 00-3-3.87
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(23f, 21f)
                verticalLineToRelative(-2f)
                arcToRelative(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    -3f,
                    -3.87f,
                )
            }
            // path: M16 3.13a4 4 0 010 7.75
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(16f, 3.13f)
                arcToRelative(
                    4f,
                    4f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    0f,
                    7.75f,
                )
            }
        }.build()
    }
}
