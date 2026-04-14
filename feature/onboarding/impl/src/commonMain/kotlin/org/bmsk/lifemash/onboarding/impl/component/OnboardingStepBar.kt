package org.bmsk.lifemash.onboarding.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
internal fun OnboardingStepBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.xxs),
    ) {
        repeat(totalSteps) { index ->
            val stepNumber = index + 1
            val color: Color = when {
                stepNumber < currentStep -> MaterialTheme.colorScheme.primary
                stepNumber == currentStep -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.outlineVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(LifeMashSpacing.micro))
                    .background(color),
            )
        }
    }
}
