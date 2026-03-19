package org.bmsk.lifemash.auth.ui

import androidx.compose.runtime.Composable

@Composable
internal actual fun rememberKakaoLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    return { onResult(Result.failure(UnsupportedOperationException("카카오 로그인은 iOS에서 아직 지원되지 않습니다."))) }
}
