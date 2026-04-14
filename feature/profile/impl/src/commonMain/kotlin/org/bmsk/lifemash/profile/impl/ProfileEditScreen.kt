package org.bmsk.lifemash.profile.impl

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.component.AvatarSize
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.component.LifeMashSegmentControl
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing

@Composable
fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onDefaultSubTabChange: (Int) -> Unit,
    onMyCalendarViewChange: (Int) -> Unit,
    onOthersCalendarViewChange: (Int) -> Unit,
    onDefaultVisibilityChange: (Int) -> Unit,
    onPickImage: () -> Unit = {},
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = "취소",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "프로필 편집",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(LifeMashSpacing.xl), strokeWidth = LifeMashSpacing.micro)
            } else {
                TextButton(onClick = onSave) {
                    Text(
                        text = "저장",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // 아바타 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LifeMashSpacing.xxxl, bottom = LifeMashSpacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                LifeMashAvatar(
                    name = uiState.name,
                    imageUrl = uiState.profileImageUrl,
                    size = AvatarSize.XXLarge,
                    onEditClick = onPickImage,
                )
            }

            // 폼 섹션
            Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.xl)) {
                FormLabel("이름")
                LifeMashInput(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    placeholder = "이름을 입력하세요",
                )
                FormLabel("아이디")
                LifeMashInput(
                    value = uiState.username,
                    onValueChange = onUsernameChange,
                    placeholder = "@아이디",
                )
                Text(
                    text = "영문·숫자·밑줄(_)·점(.) 사용 가능",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = LifeMashSpacing.xxs),
                )
                FormLabel("소개")
                LifeMashInput(
                    value = uiState.bio,
                    onValueChange = onBioChange,
                    placeholder = "소개를 입력하세요",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                )
                Spacer(Modifier.height(LifeMashSpacing.xl))
            }

            // 구분선
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LifeMashSpacing.sm)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            // 설정 섹션
            Column(modifier = Modifier.padding(horizontal = LifeMashSpacing.xl)) {
                Spacer(Modifier.height(LifeMashSpacing.md))
                Text(
                    text = "프로필 설정",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = LifeMashSpacing.xs),
                )

                SettingRow(
                    label = "프로필 시작 화면",
                    subLabel = "프로필 탭 진입 시 기본으로 보이는 화면",
                    options = listOf("순간", "캘린더"),
                    selectedIndex = uiState.defaultSubTab,
                    onSelect = onDefaultSubTabChange,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                SettingRow(
                    label = "내가 보는 방식",
                    subLabel = "내 캘린더를 내가 볼 때",
                    options = listOf("점", "칩"),
                    selectedIndex = uiState.myCalendarView,
                    onSelect = onMyCalendarViewChange,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                SettingRow(
                    label = "남들이 보는 방식",
                    subLabel = "다른 사람이 내 프로필 볼 때",
                    options = listOf("점", "칩"),
                    selectedIndex = uiState.othersCalendarView,
                    onSelect = onOthersCalendarViewChange,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                SettingRow(
                    label = "공개 일정 기본값",
                    subLabel = "새 일정 추가 시 기본 공개 범위",
                    options = listOf("전체", "친구", "비공개"),
                    selectedIndex = uiState.defaultVisibility,
                    onSelect = onDefaultVisibilityChange,
                )
                Spacer(Modifier.height(LifeMashSpacing.xl))
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = LifeMashSpacing.lg, bottom = LifeMashSpacing.xs),
    )
}

@Composable
private fun SettingRow(
    label: String,
    subLabel: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(vertical = LifeMashSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = subLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = LifeMashSpacing.micro),
            )
        }

        LifeMashSegmentControl(
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            equalWidth = false,
        )
    }
}
