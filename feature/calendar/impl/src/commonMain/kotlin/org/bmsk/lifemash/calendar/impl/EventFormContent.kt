package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.designsystem.component.LifeMashCenterTopBar
import org.bmsk.lifemash.designsystem.component.LifeMashChip
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.bmsk.lifemash.domain.calendar.EventVisibility

@Composable
internal fun EventFormContent(
    uiState: EventCreateUiState,
    isEdit: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTitleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onColorSelect: (String?) -> Unit,
    onSwitchTab: (EventCreateTab) -> Unit,
    onShowVisibilitySheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LifeMashCenterTopBar(
            title = if (isEdit) "일정 수정" else "새 일정",
            navigationIcon = {
                TextButton(onClick = onCancel) {
                    Text(
                        text = "취소",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            actions = {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(LifeMashSpacing.xl),
                        strokeWidth = LifeMashSpacing.micro,
                    )
                } else {
                    TextButton(
                        onClick = onSave,
                        enabled = uiState.isSaveEnabled,
                    ) {
                        Text(
                            text = "저장",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (uiState.isSaveEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // 제목 인라인 입력
            TextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LifeMashSpacing.xl,
                        end = LifeMashSpacing.xl,
                        top = LifeMashSpacing.xl,
                        bottom = LifeMashSpacing.lg,
                    ),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                placeholder = {
                    Text(
                        text = "어떤 일정인가요?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                singleLine = true,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 날짜/시간 행
            EventFormRow(
                icon = Icons.Outlined.CalendarMonth,
                onClick = { onSwitchTab(EventCreateTab.DATE_TIME) },
            ) {
                Column {
                    Text(
                        text = uiState.eventDateTime.dateLabel(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = uiState.eventDateTime.timeLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.eventDateTime.startTime != null)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 위치 행
            EventFormRow(
                icon = Icons.Outlined.LocationOn,
                onClick = { onSwitchTab(EventCreateTab.LOCATION) },
            ) {
                Text(
                    text = if (uiState.location.isBlank()) "위치 추가" else uiState.location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (uiState.location.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                )
            }

            // 공개 범위 행
            EventFormRow(icon = Icons.Outlined.People, onClick = onShowVisibilitySheet) {
                Text(
                    text = "공개 범위",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                LifeMashChip(
                    text = uiState.visibility.label(),
                    selected = true,
                    onClick = onShowVisibilitySheet,
                )
            }

            // 색상 행
            EventFormRow(icon = Icons.Outlined.Palette, onClick = null) {
                Text(
                    text = "색상",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    EVENT_COLORS.forEach { (hex, color) ->
                        val isSelected = uiState.selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) Modifier.border(
                                        width = 2.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape,
                                    ) else Modifier,
                                )
                                .clickable { onColorSelect(if (isSelected) null else hex) },
                        )
                    }
                }
            }

            // 메모 행
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = LifeMashSpacing.micro)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextField(
                    value = uiState.memo,
                    onValueChange = onMemoChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    placeholder = {
                        Text(
                            text = "메모 추가",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    singleLine = false,
                    minLines = 1,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

internal fun EventVisibility.label(): String = when (this) {
    EventVisibility.Public -> "전체공개"
    EventVisibility.Followers -> "팔로워"
    is EventVisibility.Group -> "그룹"
    is EventVisibility.Specific -> "특정인"
    EventVisibility.Private -> "비공개"
}

@Composable
private fun EventFormRow(
    icon: ImageVector,
    onClick: (() -> Unit)? = {},
    showChevron: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
        if (onClick != null && showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(LifeMashSpacing.lg),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
