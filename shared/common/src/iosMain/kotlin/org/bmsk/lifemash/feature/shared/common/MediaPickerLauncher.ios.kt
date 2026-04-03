package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

@Composable
actual fun rememberMediaPickerLauncher(
    maxItems: Int,
    onResult: (List<PickedMedia>) -> Unit,
): () -> Unit = { /* iOS 미디어 피커는 iosApp에서 Swift interop으로 구현 예정 */ }

@Composable
actual fun rememberSingleImagePickerLauncher(
    onResult: (PickedMedia?) -> Unit,
): () -> Unit = { /* iOS 단일 이미지 피커는 iosApp에서 Swift interop으로 구현 예정 */ }
