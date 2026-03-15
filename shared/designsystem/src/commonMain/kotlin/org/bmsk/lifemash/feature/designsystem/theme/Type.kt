package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal expect val NotoSansKR: FontFamily

internal val LifeMashTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp, lineHeight = 32.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Bold,
        fontSize = 15.sp, lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansKR, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
)
