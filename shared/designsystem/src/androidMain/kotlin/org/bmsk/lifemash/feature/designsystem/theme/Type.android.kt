package org.bmsk.lifemash.feature.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import org.bmsk.lifemash.feature.designsystem.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val notoSansKR = GoogleFont("Noto Sans KR")

internal actual val NotoSansKR: FontFamily = FontFamily(
    Font(googleFont = notoSansKR, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = notoSansKR, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = notoSansKR, fontProvider = fontProvider, weight = FontWeight.Bold),
    Font(googleFont = notoSansKR, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
)
