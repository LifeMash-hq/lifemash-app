package org.bmsk.lifemash.onboarding.impl

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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonSize
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.onboarding.impl.component.OnboardingStepBar
import androidx.compose.ui.graphics.Color

@Composable
internal fun PermissionScreen(
    uiState: OnboardingUiState,
    onBackClick: () -> Unit,
    onCalendarConnect: () -> Unit,
    onNotificationAllow: () -> Unit,
    onStartClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingStepBar(totalSteps = 3, currentStep = 3)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.sm, vertical = LifeMashSpacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = LifeMashSpacing.xl),
        ) {
            Spacer(modifier = Modifier.height(LifeMashSpacing.xxl))

            Text(
                text = "LifeMash가\n잘 동작하려면",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(LifeMashSpacing.sm))
            Text(
                text = "아래 권한을 허용하면 더 풍부한\n경험을 즐길 수 있어요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(LifeMashSpacing.xxxl))

            // 캘린더 연동 카드
            PermissionCard(
                iconContent = {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(LifeMashSpacing.xxl),
                    )
                },
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                title = "캘린더 연동",
                description = "기기 일정을 불러와\n자동으로 연결해요",
                isConnected = uiState.calendarConnected,
                connectLabel = "연결하기",
                connectedLabel = "연결됨 ✓",
                onActionClick = onCalendarConnect,
            )

            Spacer(modifier = Modifier.height(LifeMashSpacing.md))

            // 알림 허용 카드
            PermissionCard(
                iconContent = {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(LifeMashSpacing.xxl),
                    )
                },
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                title = "알림 허용",
                description = "친구 소식과 일정 리마인드를\n놓치지 않을 수 있어요",
                isConnected = uiState.notificationAllowed,
                connectLabel = "허용하기",
                connectedLabel = "허용됨 ✓",
                onActionClick = onNotificationAllow,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LifeMashButton(
                text = "LifeMash 시작하기 🎉",
                onClick = onStartClick,
                style = LifeMashButtonStyle.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(LifeMashSpacing.md))
            TextButton(onClick = onSkipClick) {
                Text(
                    text = "나중에 설정할게요",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    iconContent: @Composable () -> Unit,
    iconBgColor: Color,
    title: String,
    description: String,
    isConnected: Boolean,
    connectLabel: String,
    connectedLabel: String,
    onActionClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(LifeMashRadius.lg),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                iconContent()
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(LifeMashSpacing.micro))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LifeMashButton(
                text = if (isConnected) connectedLabel else connectLabel,
                onClick = onActionClick,
                style = if (isConnected) LifeMashButtonStyle.Secondary else LifeMashButtonStyle.Ghost,
                size = LifeMashButtonSize.Small,
            )
        }
    }
}