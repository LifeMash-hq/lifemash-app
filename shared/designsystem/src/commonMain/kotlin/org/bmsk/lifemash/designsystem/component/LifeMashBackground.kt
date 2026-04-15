package org.bmsk.lifemash.designsystem.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 앱의 기본 배경 컨테이너.
 *
 * `LifeMashTheme`이 색 토큰만 제공하는 것과 짝을 이뤄, 실제 배경 픽셀을 그리는 역할을 맡는다.
 * 앱 루트(MainActivity)와 Preview에서 공통으로 사용해 실사용 환경과 Preview 렌더링을 일치시킨다.
 *
 * 사용:
 * ```
 * LifeMashTheme {
 *     LifeMashBackground {
 *         // 화면
 *     }
 * }
 * ```
 */
@Composable
fun LifeMashBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        content = content,
    )
}
