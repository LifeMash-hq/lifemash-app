package org.bmsk.lifemash.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal expect val Pretendard: FontFamily

internal val LifeMashTypography = Typography(
    // tokens.css: 3xl = 32sp
    displayLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: 2xl = 24sp
    displayMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp,
    ),
    // tokens.css: xl = 20sp
    displaySmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),
    // tokens.css: xl = 20sp
    headlineLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: lg = 17sp
    headlineMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: base = 15sp
    headlineSmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: lg = 17sp
    titleLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Bold,
        fontSize = 17.sp, lineHeight = 24.sp,
    ),
    // tokens.css: base = 15sp
    titleMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 22.sp,
    ),
    // tokens.css: md = 14sp
    titleSmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    // tokens.css: base = 15sp
    bodyLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: md = 14sp
    bodyMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: sm = 12sp
    bodySmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: sm = 12sp
    labelLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp,
    ),
    // tokens.css: sm = 12sp
    labelMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    // tokens.css: xs = 10sp
    labelSmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.sp,
    ),
)
