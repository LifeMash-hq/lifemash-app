package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

@Composable
actual fun rememberShareLauncher(): (text: String) -> Unit =
    { /* iOS 공유는 iosApp에서 Swift interop으로 구현 예정 */ }
