package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LightDefaultColorScheme = lightColorScheme(
    primary = AccentSlate,
    onPrimary = Color.White,
    primaryContainer = BgElevated,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE5E5),
    onSecondaryContainer = Color(0xFF7A0000),
    tertiary = AccentGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF052E16),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRedContainer,
    onErrorContainer = OnErrorRedContainer,
    background = BgPage,
    onBackground = TextPrimary,
    surface = BgPage,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkTextPrimary,
    outline = TextTertiary,
    outlineVariant = BorderDefault,
)

val DarkDefaultColorScheme = darkColorScheme(
    primary = DarkAccentSlate,
    onPrimary = DarkSurface,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5C1010),
    onSecondaryContainer = Color(0xFFFFB3B3),
    tertiary = AccentGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF0A3D1F),
    onTertiaryContainer = Color(0xFFBBF7D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkSurface,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    inverseSurface = BgElevated,
    inverseOnSurface = TextPrimary,
    outline = DarkTextSecondary,
    outlineVariant = DarkBorder,
)

val LightAndroidGradientColors = GradientColors(container = BgElevated)
val DarkAndroidGradientColors = GradientColors(container = Color.Black)

val LightAndroidBackgroundTheme = BackgroundTheme(color = BgPage)
val DarkAndroidBackgroundTheme = BackgroundTheme(color = DarkSurface)

@Composable
fun LifeMashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme

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
