package org.bmsk.lifemash.feature.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.feature.designsystem.theme.AccentBlue
import org.bmsk.lifemash.feature.designsystem.theme.AccentCoral
import org.bmsk.lifemash.feature.designsystem.theme.AccentGreen
import org.bmsk.lifemash.feature.designsystem.theme.AccentIndigo
import org.bmsk.lifemash.feature.designsystem.theme.AccentOrange
import org.bmsk.lifemash.feature.designsystem.theme.AccentSlate
import org.bmsk.lifemash.feature.designsystem.theme.AccentTeal
import org.bmsk.lifemash.feature.designsystem.theme.AccentYellow
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashRadius

enum class ArticleCategory(val label: String) {
    POLITICS("정치"),
    ECONOMY("경제"),
    TECH("기술"),
    WORLD("세계"),
    CULTURE("문화"),
    SOCIETY("사회"),
    SPORTS("스포츠"),
    ALL("전체"),
}

val defaultCategoryColorMap: Map<ArticleCategory, Color> = mapOf(
    ArticleCategory.POLITICS to AccentCoral,
    ArticleCategory.ECONOMY to AccentGreen,
    ArticleCategory.TECH to AccentIndigo,
    ArticleCategory.WORLD to AccentBlue,
    ArticleCategory.CULTURE to AccentYellow,
    ArticleCategory.SOCIETY to AccentOrange,
    ArticleCategory.SPORTS to AccentTeal,
    ArticleCategory.ALL to AccentSlate,
)

@Composable
fun CategoryBadge(
    category: ArticleCategory,
    modifier: Modifier = Modifier,
    colorMap: Map<ArticleCategory, Color> = defaultCategoryColorMap,
) {
    val bgColor = colorMap[category] ?: AccentSlate
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(LifeMashRadius.xs))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}
