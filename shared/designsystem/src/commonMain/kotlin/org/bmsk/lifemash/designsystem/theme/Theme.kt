package org.bmsk.lifemash.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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
    val overlay: Color = Color.Unspecified,
    val onDanger: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val textDisabled: Color = Color.Unspecified,
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
    overlay = Overlay,
    onDanger = OnDanger,
    onSuccess = OnSuccess,
    textDisabled = TextDisabled,
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
    overlay = DarkOverlay,
    onDanger = OnDanger,
    onSuccess = OnSuccess,
    textDisabled = DarkTextDisabled,
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

@Composable
fun LifeMashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    PlatformThemeEffect(darkTheme)

    CompositionLocalProvider(
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
