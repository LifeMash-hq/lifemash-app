package org.bmsk.lifemash.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import org.bmsk.lifemash.designsystem.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val pretendard = GoogleFont("Pretendard")

internal actual val Pretendard: FontFamily = FontFamily(
    Font(googleFont = pretendard, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = pretendard, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = pretendard, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = pretendard, fontProvider = fontProvider, weight = FontWeight.Bold),
)
