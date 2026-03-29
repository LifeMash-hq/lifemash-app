package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Extended semantic colors (tokens.css 전용, Material3에 없는 색상) ────────

@Immutable
data class LifeMashSemanticColors(
    val chipBg: Color = Color.Unspecified,
    val inputBg: Color = Color.Unspecified,
    val unreadBg: Color = Color.Unspecified,
    val navBg: Color = Color.Unspecified,
    val navBorder: Color = Color.Unspecified,
    val pageBg: Color = Color.Unspecified,
    val primaryLight: Color = Color.Unspecified,
    val primaryDark: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
    val info: Color = Color.Unspecified,
    val danger: Color = Color.Unspecified,
    val success: Color = Color.Unspecified,
)

val LocalLifeMashColors = staticCompositionLocalOf { LifeMashSemanticColors() }

private val LightSemanticColors = LifeMashSemanticColors(
    chipBg = ChipBg,
    inputBg = InputBg,
    unreadBg = UnreadBg,
    navBg = NavBg,
    navBorder = NavBorder,
    pageBg = PageBg,
    primaryLight = PrimaryLight,
    primaryDark = PrimaryDark,
    warning = Warning,
    info = Info,
    danger = Danger,
    success = Success,
)

private val DarkSemanticColors = LifeMashSemanticColors(
    chipBg = DarkChipBg,
    inputBg = DarkInputBg,
    unreadBg = DarkUnreadBg,
    navBg = DarkNavBg,
    navBorder = DarkNavBorder,
    pageBg = DarkPageBg,
    primaryLight = DarkPrimaryLight,
    primaryDark = DarkPrimaryDark,
    warning = Warning,
    info = Info,
    danger = Danger,
    success = Success,
)

// ── Material3 Color Schemes ─────────────────────────────────────────────────

val LightDefaultColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextPrimary,
    secondary = Success,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF052E16),
    tertiary = Info,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBE5FF),
    onTertiaryContainer = Color(0xFF001A41),
    error = Danger,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = BgPage,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = ChipBg,
    onSurfaceVariant = TextSecondary,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkTextPrimary,
    outline = TextSecondary,
    outlineVariant = Divider,
)

val DarkDefaultColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkBg,
    primaryContainer = DarkPrimaryLight,
    onPrimaryContainer = DarkTextPrimary,
    secondary = Success,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0A3D1F),
    onSecondaryContainer = Color(0xFFBBF7D0),
    tertiary = Info,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF002966),
    onTertiaryContainer = Color(0xFFDBE5FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkChipBg,
    onSurfaceVariant = DarkTextSecondary,
    inverseSurface = Surface,
    inverseOnSurface = TextPrimary,
    outline = DarkTextSecondary,
    outlineVariant = DarkDivider,
)

val LightAndroidGradientColors = GradientColors(container = PrimaryLight)
val DarkAndroidGradientColors = GradientColors(container = Color.Black)

val LightAndroidBackgroundTheme = BackgroundTheme(color = BgPage)
val DarkAndroidBackgroundTheme = BackgroundTheme(color = DarkBg)

@Composable
fun LifeMashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val gradientColors = when {
        !disableDynamicTheming -> emptyGradientColors
        else -> if (darkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
    }
    val backgroundTheme = if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
    val tintTheme = TintTheme()

    PlatformThemeEffect(darkTheme)

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
        LocalLifeMashColors provides semanticColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LifeMashTypography,
            shapes = LifeMashShapes,
            content = content,
        )
    }
}

@Composable
internal expect fun PlatformThemeEffect(darkTheme: Boolean)
