package org.bmsk.lifemash.onboarding.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.domain.onboarding.HandleValidationStatus
import org.bmsk.lifemash.onboarding.impl.component.OnboardingStepBar

@Composable
internal fun ProfileSetupScreen(
    uiState: OnboardingUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onHandleChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingStepBar(totalSteps = 3, currentStep = 2)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
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
            Text(
                text = "프로필 설정",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.lg),
        ) {
            // 아바타 피커
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = uiState.name.firstOrNull()?.uppercase() ?: "나",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(LifeMashSpacing.xs))
                Text(
                    text = "사진을 탭해서 변경",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(LifeMashSpacing.xl))

            // 이름 필드
            LifeMashInput(
                value = uiState.name,
                onValueChange = onNameChange,
                label = "이름",
                placeholder = "홍길동",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(LifeMashSpacing.lg))

            // 아이디 필드
            val handleError = when (uiState.handleStatus) {
                HandleValidationStatus.TAKEN -> "이미 사용 중인 아이디예요"
                HandleValidationStatus.INVALID_FORMAT -> "영소문자, 숫자, 밑줄(_) 3~15자로 입력해주세요"
                else -> null
            }
            val handleHelper = when (uiState.handleStatus) {
                HandleValidationStatus.CHECKING -> "확인 중..."
                HandleValidationStatus.AVAILABLE -> "✓ 사용 가능한 아이디예요"
                else -> null
            }
            LifeMashInput(
                value = uiState.handle,
                onValueChange = onHandleChange,
                label = "아이디",
                placeholder = "lifemash_user",
                isError = handleError != null,
                errorMessage = handleError,
                helperMessage = handleHelper,
                leadingIcon = {
                    Text(
                        text = "@",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(LifeMashSpacing.lg))

            // 생년월일 필드
            LifeMashInput(
                value = uiState.birthDate,
                onValueChange = onBirthDateChange,
                label = "생년월일",
                placeholder = "YYYY. MM. DD",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // 다음 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.lg),
        ) {
            LifeMashButton(
                text = "다음",
                onClick = onNextClick,
                style = LifeMashButtonStyle.Primary,
                enabled = uiState.isProfileValid,
                isLoading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
