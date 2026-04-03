package org.bmsk.lifemash.feature.shared.common

import androidx.compose.runtime.Composable

data class PickedMedia(
    val uri: String,
    val mimeType: String?,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
)

/**
 * 여러 미디어(이미지/영상)를 선택하는 피커 런처를 반환한다.
 * @param maxItems 최대 선택 가능 미디어 수
 * @param onResult 선택 결과 콜백
 */
@Composable
expect fun rememberMediaPickerLauncher(
    maxItems: Int = 10,
    onResult: (List<PickedMedia>) -> Unit,
): () -> Unit

/**
 * 단일 이미지를 선택하는 피커 런처를 반환한다.
 * @param onResult 선택 결과 콜백 (취소 시 null)
 */
@Composable
expect fun rememberSingleImagePickerLauncher(
    onResult: (PickedMedia?) -> Unit,
): () -> Unit
