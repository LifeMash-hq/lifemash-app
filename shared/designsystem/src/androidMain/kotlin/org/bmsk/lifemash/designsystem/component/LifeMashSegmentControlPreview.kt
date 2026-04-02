package org.bmsk.lifemash.designsystem.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LifeMashTheme

private val twoOptions = listOf("일정", "할 일")
private val threeOptions = listOf("전체", "팀", "개인")

@Preview(name = "Light - 첫 번째 선택", showBackground = true)
@Preview(name = "Dark - 첫 번째 선택", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun LifeMashSegmentControlPreview_FirstSelected() {
    LifeMashTheme {
        LifeMashSegmentControl(
            options = threeOptions,
            selectedIndex = 0,
            onSelect = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.lg),
        )
    }
}

@Preview(name = "Light - 중간 선택", showBackground = true)
@Preview(name = "Dark - 중간 선택", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun LifeMashSegmentControlPreview_MiddleSelected() {
    LifeMashTheme {
        LifeMashSegmentControl(
            options = threeOptions,
            selectedIndex = 1,
            onSelect = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.lg),
        )
    }
}

@Preview(name = "Light - 마지막 선택", showBackground = true)
@Preview(name = "Dark - 마지막 선택", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun LifeMashSegmentControlPreview_LastSelected() {
    LifeMashTheme {
        LifeMashSegmentControl(
            options = threeOptions,
            selectedIndex = 2,
            onSelect = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.lg),
        )
    }
}

@Preview(name = "Light - 2개 옵션", showBackground = true)
@Preview(name = "Dark - 2개 옵션", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun LifeMashSegmentControlPreview_TwoOptions() {
    LifeMashTheme {
        LifeMashSegmentControl(
            options = twoOptions,
            selectedIndex = 0,
            onSelect = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.lg),
        )
    }
}
