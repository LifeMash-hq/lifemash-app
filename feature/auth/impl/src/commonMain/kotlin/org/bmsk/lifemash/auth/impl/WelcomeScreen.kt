package org.bmsk.lifemash.auth.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashLogo
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.designsystem.theme.LifeMashGradient

@Composable
internal fun WelcomeScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeMashGradient.primaryBrush())
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 상단 영역 — 로고 + 앱 이름 + 태그라인
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = LifeMashSpacing.sm,
                        shape = RoundedCornerShape(LifeMashSpacing.xxl),
                        ambientColor = onPrimary.copy(alpha = 0.1f),
                    )
                    .clip(RoundedCornerShape(LifeMashSpacing.xxl))
                    .background(onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                LifeMashLogo(size = 50.dp)
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xxl))

            Text(
                text = "LifeMash",
                style = MaterialTheme.typography.displayLarge,
                color = onPrimary,
            )

            Spacer(modifier = Modifier.height(LifeMashSpacing.md))

            Text(
                text = "일정을 공유하고\n순간을 함께 나누세요",
                style = MaterialTheme.typography.bodyLarge,
                color = onPrimary.copy(alpha = 0.82f),
                textAlign = TextAlign.Center,
            )
        }

        // 하단 영역 — 인디케이터 + 버튼
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = LifeMashSpacing.xxl,
                    vertical = LifeMashSpacing.huge,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
        ) {
            // 3-dot 인디케이터
            Row(
                horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 활성 dot (더 넓음)
                Box(
                    modifier = Modifier
                        .width(LifeMashSpacing.xl)
                        .height(LifeMashSpacing.xs)
                        .clip(CircleShape)
                        .background(onPrimary),
                )
                // 비활성 dot
                Box(
                    modifier = Modifier
                        .size(LifeMashSpacing.xs)
                        .clip(CircleShape)
                        .background(onPrimary.copy(alpha = 0.35f)),
                )
                Box(
                    modifier = Modifier
                        .size(LifeMashSpacing.xs)
                        .clip(CircleShape)
                        .background(onPrimary.copy(alpha = 0.35f)),
                )
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xl))

            // "시작하기" 버튼
            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = onPrimary,
                    contentColor = primary,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = LifeMashSpacing.xxs,
                ),
            ) {
                Text(
                    text = "시작하기",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            // "이미 계정이 있어요" 버튼
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = onPrimary.copy(alpha = 0.15f),
                    contentColor = onPrimary,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                ),
            ) {
                Text(
                    text = "이미 계정이 있어요",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
