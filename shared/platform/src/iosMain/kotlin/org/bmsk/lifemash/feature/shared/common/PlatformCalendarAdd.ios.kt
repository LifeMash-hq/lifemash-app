package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

@Composable
actual fun rememberAddToCalendarLauncher(): (
    title: String,
    startMillis: Long,
    endMillis: Long?,
    location: String?,
    description: String?,
) -> Unit = { _, _, _, _, _ -> /* iOS 캘린더 추가는 iosApp에서 Swift interop으로 구현 예정 */ }
