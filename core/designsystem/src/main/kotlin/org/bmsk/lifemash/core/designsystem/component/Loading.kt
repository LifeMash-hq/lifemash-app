@file:Suppress("MaximumLineLength", "MaxLineLength")

package org.bmsk.lifemash.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.flatten
import org.bmsk.lifemash.core.designsystem.component.LifeMashPath.path
import org.bmsk.lifemash.core.designsystem.theme.LifeMashTheme
import kotlin.math.floor

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        path.forEach { PathCanvas(it) }
    }
}

@Composable
private fun PathCanvas(
    path: Path,
) {
    val bounds = path.getBounds()

    val totalLength = remember {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        pathMeasure.length
    }
    val lines = remember {
        path.asAndroidPath().flatten(0.5f)
    }
    val progress = remember {
        Animatable(0f)
    }
    LaunchedEffect(Unit) {
        progress.animateTo(
            10f, animationSpec = infiniteRepeatable(tween(1500))
        )
    }
    val scaleFactor = 7f // Scale factor to prevent overlapping

    Canvas(
        modifier = Modifier
            .aspectRatio(bounds.width * scaleFactor / bounds.height * scaleFactor),
        onDraw = {
            val currentLength = totalLength * progress.value
            lines.forEach { line ->
                if (line.startFraction * totalLength < currentLength) {
                    val startColor = interpolateColors(line.startFraction, colors)
                    val endColor = interpolateColors(line.endFraction, colors)
                    drawLine(
                        brush = Brush.linearGradient(listOf(startColor, endColor)),
                        start = Offset(line.start.x * scaleFactor, line.start.y * scaleFactor),
                        end = Offset(line.end.x * scaleFactor, line.end.y * scaleFactor),
                        strokeWidth = 1.7f * scaleFactor, // Adjust stroke width accordingly
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    )
}

private val colors = listOf(
    Color(0xFF42A5F5), // Light Blue
    Color(0xFFAB47BC), // Purple
    Color(0xFFEC407A), // Pink
    Color(0xFF7E57C2), // Deep Purple
    Color(0xFF26C6DA), // Cyan
    Color(0xFF66BB6A), // Green
    Color(0xFFFFCA28), // Amber
    Color(0xFFFF7043), // Deep Orange
    Color(0xFF8D6E63), // Brown
    Color(0xFF26A69A), // Teal
    Color(0xFFEF5350), // Red
    Color(0xFFFFEE58), // Yellow
)

object LifeMashPath {
    val pathStrings = listOf(
//        PathParser().parsePathString(
//            "M-0.5,-0.5C66.17,-0.5 132.83,-0.5 199.5,-0.5C199.5,32.83 199.5,66.17 199.5,99.5C132.83,99.5 66.17,99.5 -0.5,99.5C-0.5,66.17 -0.5,32.83 -0.5,-0.5Z"
//        ),
        PathParser().parsePathString(
            "M46.5,39.5C47.5,39.5 48.5,39.5 49.5,39.5C49.5,44.83 49.5,50.17 49.5,55.5C52.57,55.18 55.57,55.52 58.5,56.5C54.55,57.49 50.55,57.82 46.5,57.5C46.5,51.5 46.5,45.5 46.5,39.5Z"
        ),
        PathParser().parsePathString(
            "M60.5,39.5C61.67,39.28 62.67,39.61 63.5,40.5C61.95,41.8 60.95,41.46 60.5,39.5Z"
        ),
        PathParser().parsePathString(
            "M69.5,38.5C71.6,38.2 73.6,38.53 75.5,39.5C73.95,40.45 72.29,40.79 70.5,40.5C70.5,41.5 70.5,42.5 70.5,43.5C75.83,44.17 75.83,44.83 70.5,45.5C70.5,49.5 70.5,53.5 70.5,57.5C69.5,57.5 68.5,57.5 67.5,57.5C67.77,53.1 67.44,48.76 66.5,44.5C67.51,42.47 68.51,40.47 69.5,38.5Z"
        ),
        PathParser().parsePathString(
            "M60.5,43.5C61.5,43.5 62.5,43.5 63.5,43.5C63.5,48.17 63.5,52.83 63.5,57.5C62.5,57.5 61.5,57.5 60.5,57.5C60.5,52.83 60.5,48.17 60.5,43.5Z"
        ),
        PathParser().parsePathString(
            "M91.5,39.5C93.14,39.29 94.64,39.62 96,40.5C96.96,43.38 98.29,46.04 100,48.5C101.86,46.11 103.19,43.45 104,40.5C105.36,39.62 106.86,39.29 108.5,39.5C108.5,45.5 108.5,51.5 108.5,57.5C107.5,57.5 106.5,57.5 105.5,57.5C105.67,52.82 105.5,48.15 105,43.5C103.26,46.31 102.09,49.31 101.5,52.5C100.32,52.72 99.32,52.39 98.5,51.5C97.81,48.91 96.64,46.58 95,44.5C94.5,48.82 94.33,53.15 94.5,57.5C93.5,57.5 92.5,57.5 91.5,57.5C91.5,51.5 91.5,45.5 91.5,39.5Z"
        ),
        PathParser().parsePathString(
            "M113.5,43.5C116.93,43.11 120.1,43.77 123,45.5C123.5,49.49 123.67,53.49 123.5,57.5C120.99,56.33 118.49,56.33 116,57.5C110.67,55.67 110.17,52.84 114.5,49C116.58,48.81 118.58,48.31 120.5,47.5C118.75,45.19 116.58,44.86 114,46.5C112.32,45.82 112.15,44.82 113.5,43.5Z"
        ),
        PathParser().parsePathString(
            "M129.5,43.5C132.48,43.1 135.15,43.77 137.5,45.5C135.93,46.45 134.26,46.45 132.5,45.5C131,45.45 130,46.12 129.5,47.5C131.51,49.11 133.85,50.27 136.5,51C138.14,53.78 137.47,55.78 134.5,57C132.17,57.67 129.83,57.67 127.5,57C127.04,56.59 126.71,56.09 126.5,55.5C128.99,55.22 131.65,55.22 134.5,55.5C134.67,54.18 134.34,53.01 133.5,52C126.17,51.48 124.84,48.65 129.5,43.5Z"
        ),
        PathParser().parsePathString(
            "M140.5,38.5C141.5,38.5 142.5,38.5 143.5,38.5C143.34,40.53 143.51,42.53 144,44.5C147.39,42.73 150.06,43.39 152,46.5C152.5,50.15 152.66,53.82 152.5,57.5C151.5,57.5 150.5,57.5 149.5,57.5C149.66,53.82 149.5,50.15 149,46.5C147.54,45.25 146.04,45.25 144.5,46.5C143.51,50.11 143.18,53.77 143.5,57.5C142.5,57.5 141.5,57.5 140.5,57.5C140.5,51.17 140.5,44.83 140.5,38.5Z"
        ),
        PathParser().parsePathString(
            "M79.5,43.5C85.19,42.53 88.19,44.86 88.5,50.5C85.17,50.5 81.83,50.5 78.5,50.5C78.3,52.39 78.96,53.89 80.5,55C82.94,54.95 85.27,55.11 87.5,55.5C83.05,58.56 79.22,57.9 76,53.5C75.33,51.5 75.33,49.5 76,47.5C77.38,46.29 78.54,44.96 79.5,43.5Z"
        ),
        PathParser().parsePathString("M80.5,45.5C82.66,45.12 84.33,45.78 85.5,47.5C83.17,48.83 80.83,48.83 78.5,47.5C79.24,46.82 79.91,46.15 80.5,45.5Z"),
        PathParser().parsePathString("M115.5,50.5C117.17,50.5 118.83,50.5 120.5,50.5C120.99,55.16 119.15,56.49 115,54.5C114.42,53.07 114.59,51.74 115.5,50.5Z"),
    )

    val path = pathStrings.map { it.toPath() }
}

private fun interpolateColors(
    progress: Float,
    colorsInput: List<Color>,
): Color {
    if (progress == 1f) return colorsInput.last()

    val scaledProgress = progress * (colorsInput.size - 1)
    val oldColor = colorsInput[scaledProgress.toInt()]
    val newColor = colorsInput[(scaledProgress + 1f).toInt()]
    val newScaledAnimationValue = scaledProgress - floor(scaledProgress)
    return lerp(start = oldColor, stop = newColor, fraction = newScaledAnimationValue)
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun LifeMashSplashPreview() {
    LifeMashTheme {
        Loading(modifier = Modifier.size(width = 1000.dp, height = 500.dp))
    }
}