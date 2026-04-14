package org.bmsk.lifemash.calendar.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashEmptyState
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.component.LifeMashSegmentControl
import org.bmsk.lifemash.designsystem.component.LifeMashSkeleton
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.koin.compose.viewmodel.koinViewModel

// 디자인 스펙 아이콘 배경색 (데코레이션 상수, 라이트/다크 공통 적용)
private val VIS_COLOR_PUBLIC = Color(0xFF0984E3)
private val VIS_COLOR_FOLLOWERS = Color(0xFF6C5CE7)
private val VIS_COLOR_GROUP = Color(0xFF00B894)
private val VIS_COLOR_SPECIFIC = Color(0xFF636E72)
private val VIS_COLOR_PRIVATE = Color(0xFF2D3436)

private sealed interface VisibilitySheetStep {
    data object PickType : VisibilitySheetStep
    data object GroupList : VisibilitySheetStep
    data object CreateGroup : VisibilitySheetStep
    data object SelectPeople : VisibilitySheetStep
}

private data class VisOption(
    val visibility: EventVisibility?,  // null = 네비게이션 전용
    val label: String,
    val description: String,
    val iconBg: Color,
    val icon: ImageVector,
    val navStep: VisibilitySheetStep? = null,
)

private val VIS_OPTIONS = listOf(
    VisOption(EventVisibility.Public, "전체공개", "누구든지 볼 수 있어요", VIS_COLOR_PUBLIC, Icons.Outlined.Language),
    VisOption(EventVisibility.Followers, "팔로워", "나를 팔로우하는 사람들", VIS_COLOR_FOLLOWERS, Icons.Outlined.People),
    VisOption(null, "그룹", "멤버끼리 서로 공유하는 공간", VIS_COLOR_GROUP, Icons.Outlined.GroupAdd, VisibilitySheetStep.GroupList),
    VisOption(null, "특정인", "내가 직접 고른 사람들만", VIS_COLOR_SPECIFIC, Icons.Outlined.Person, VisibilitySheetStep.SelectPeople),
    VisOption(EventVisibility.Private, "비공개", "나만 볼 수 있어요", VIS_COLOR_PRIVATE, Icons.Outlined.Lock),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VisibilitySheet(
    currentVisibility: EventVisibility,
    onSelect: (EventVisibility) -> Unit,
    onDismiss: () -> Unit,
    viewModel: VisibilitySheetViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var step by remember { mutableStateOf<VisibilitySheetStep>(VisibilitySheetStep.PickType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = LifeMashRadius.lg, topEnd = LifeMashRadius.lg),
        dragHandle = null,
    ) {
        when (step) {
            VisibilitySheetStep.PickType -> VisibilityPickTypeContent(
                currentVisibility = currentVisibility,
                onSelect = { visibility ->
                    onSelect(visibility)
                    onDismiss()
                },
                onNavigate = { navStep -> step = navStep },
            )
            VisibilitySheetStep.GroupList -> when (val state = uiState) {
                is VisibilitySheetUiState.Loading -> VisibilityLoadingContent(onBack = { step = VisibilitySheetStep.PickType })
                is VisibilitySheetUiState.Ready -> GroupListContent(
                    groups = state.groups,
                    currentVisibility = currentVisibility,
                    onSelect = { groupId ->
                        onSelect(EventVisibility.Group(groupId))
                        onDismiss()
                    },
                    onCreateGroup = { step = VisibilitySheetStep.CreateGroup },
                    onBack = { step = VisibilitySheetStep.PickType },
                )
                is VisibilitySheetUiState.Error -> VisibilityErrorContent(
                    message = state.message,
                    onBack = { step = VisibilitySheetStep.PickType },
                )
            }
            VisibilitySheetStep.CreateGroup -> {
                val creationState = (uiState as? VisibilitySheetUiState.Ready)?.groupCreation
                    ?: GroupCreationState.Idle
                CreateGroupContent(
                    creationState = creationState,
                    onBack = { step = VisibilitySheetStep.GroupList },
                    onCreate = { name, type -> viewModel.createGroup(name, type) },
                )
            }
            VisibilitySheetStep.SelectPeople -> when (val state = uiState) {
                is VisibilitySheetUiState.Loading -> VisibilityLoadingContent(onBack = { step = VisibilitySheetStep.PickType })
                is VisibilitySheetUiState.Ready -> SelectPeopleContent(
                    followers = state.followers,
                    currentVisibility = currentVisibility,
                    onConfirm = { userIds ->
                        onSelect(EventVisibility.Specific(userIds))
                        onDismiss()
                    },
                    onBack = { step = VisibilitySheetStep.PickType },
                )
                is VisibilitySheetUiState.Error -> VisibilityErrorContent(
                    message = state.message,
                    onBack = { step = VisibilitySheetStep.PickType },
                )
            }
        }
    }
}

@Composable
private fun VisibilityPickTypeContent(
    currentVisibility: EventVisibility,
    onSelect: (EventVisibility) -> Unit,
    onNavigate: (VisibilitySheetStep) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "공개 범위",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                horizontal = LifeMashSpacing.xl,
                vertical = LifeMashSpacing.lg,
            ),
        )

        VIS_OPTIONS.forEachIndexed { index, opt ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = LifeMashSpacing.xl),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            VisibilityOptionRow(
                option = opt,
                isSelected = opt.visibility?.let { it == currentVisibility } ?: false,
                onClick = {
                    if (opt.navStep != null) {
                        onNavigate(opt.navStep)
                    } else if (opt.visibility != null) {
                        onSelect(opt.visibility)
                    }
                },
            )
        }

        Spacer(modifier = Modifier.size(LifeMashSpacing.xxl))
    }
}

@Composable
private fun VisibilityOptionRow(
    option: VisOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(LifeMashSpacing.huge)
                .clip(CircleShape)
                .background(option.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(LifeMashSpacing.xl),
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        when {
            isSelected -> Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(LifeMashSpacing.xl),
                tint = MaterialTheme.colorScheme.primary,
            )
            option.navStep != null -> Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(LifeMashSpacing.xl),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

// ─── Loading / Error ──────────────────────────────────────────────────────────

@Composable
private fun VisibilityLoadingContent(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        VisibilitySubScreenHeader(title = "", onBack = onBack)
        LifeMashSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl)
                .size(200.dp),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun VisibilityErrorContent(message: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        VisibilitySubScreenHeader(title = "", onBack = onBack)
        LifeMashEmptyState(
            icon = Icons.Outlined.ErrorOutline,
            title = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(LifeMashSpacing.xl),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun VisibilitySubScreenHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.sm, vertical = LifeMashSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
        }
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ─── Group List ───────────────────────────────────────────────────────────────

@Composable
private fun GroupType.toEmoji(): String = when (this) {
    GroupType.COUPLE -> "♥"
    GroupType.FAMILY -> "👨‍👩‍👧‍👦"
    GroupType.FRIENDS -> "🎓"
    GroupType.TEAM -> "💼"
}

@Composable
private fun GroupType.toBgColor(): Color = when (this) {
    GroupType.COUPLE -> MaterialTheme.colorScheme.errorContainer
    GroupType.FAMILY -> MaterialTheme.colorScheme.primaryContainer
    GroupType.FRIENDS -> MaterialTheme.colorScheme.secondaryContainer
    GroupType.TEAM -> MaterialTheme.colorScheme.tertiaryContainer
}

@Composable
internal fun GroupListContent(
    groups: List<Group>,
    currentVisibility: EventVisibility,
    onSelect: (groupId: String) -> Unit,
    onCreateGroup: () -> Unit,
    onBack: () -> Unit,
) {
    val selectedGroupId = (currentVisibility as? EventVisibility.Group)?.groupId

    Column(modifier = Modifier.fillMaxWidth()) {
        VisibilitySubScreenHeader(title = "그룹 선택", onBack = onBack)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            groups.forEachIndexed { index, group ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = LifeMashSpacing.xl),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(group.id) }
                        .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(LifeMashRadius.md))
                            .background(group.type.toBgColor()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = group.type.toEmoji(), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name ?: group.type.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "멤버 ${group.members.size}명",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            GroupTagChip(isShared = group.members.size > 1)
                        }
                    }
                    if (selectedGroupId == group.id) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(LifeMashSpacing.xl),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(LifeMashSpacing.xl),
                            tint = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = LifeMashSpacing.xl),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCreateGroup)
                    .padding(horizontal = LifeMashSpacing.xl, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(LifeMashRadius.md))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(LifeMashSpacing.xl),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "새 그룹 만들기",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun GroupTagChip(isShared: Boolean) {
    val (
        label,
        bgColor,
        textColor,
    ) = if (isShared) {
        Triple(
            "멤버 공유",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
        )
    } else {
        Triple(
            "나만 사용",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Surface(
        shape = RoundedCornerShape(LifeMashRadius.full),
        color = bgColor,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = LifeMashSpacing.sm, vertical = LifeMashSpacing.micro),
        )
    }
}

// ─── Create Group ─────────────────────────────────────────────────────────────

@Composable
internal fun CreateGroupContent(
    creationState: GroupCreationState,
    onBack: () -> Unit,
    onCreate: (name: String, type: GroupType) -> Unit,
) {
    var groupName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(GroupType.FRIENDS) }

    Column(modifier = Modifier.fillMaxWidth()) {
        VisibilitySubScreenHeader(title = "새 그룹", onBack = onBack)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LifeMashSpacing.xl),
        ) {
            Spacer(modifier = Modifier.size(LifeMashSpacing.lg))

            Text(
                text = "그룹 이름",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.size(LifeMashSpacing.sm))
            LifeMashInput(
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = "예: 친한친구, 가족, 회사팀",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.size(LifeMashSpacing.xl))

            Text(
                text = "그룹 유형",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.size(LifeMashSpacing.sm))
            LifeMashSegmentControl(
                options = listOf("연인", "가족", "친구", "팀"),
                selectedIndex = GroupType.entries.indexOf(selectedType),
                onSelect = { selectedType = GroupType.entries[it] },
                modifier = Modifier.fillMaxWidth(),
            )

            if (creationState is GroupCreationState.Failed) {
                Spacer(modifier = Modifier.size(LifeMashSpacing.md))
                Text(
                    text = creationState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.size(LifeMashSpacing.xxl))

            LifeMashButton(
                text = "그룹 만들기",
                onClick = { onCreate(groupName, selectedType) },
                enabled = groupName.isNotBlank(),
                isLoading = creationState is GroupCreationState.InProgress,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.size(LifeMashSpacing.lg))
        }
    }
}

// ─── Select People ────────────────────────────────────────────────────────────

@Composable
internal fun SelectPeopleContent(
    followers: List<Follower>,
    currentVisibility: EventVisibility,
    onConfirm: (userIds: List<String>) -> Unit,
    onBack: () -> Unit,
) {
    val initialIds = (currentVisibility as? EventVisibility.Specific)?.userIds ?: emptyList()
    var selectedIds by remember { mutableStateOf(initialIds.toSet()) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = followers.filter { it.matchesQuery(searchQuery) }

    Column(modifier = Modifier.fillMaxWidth()) {
        VisibilitySubScreenHeader(title = "특정인 선택", onBack = onBack)

        LifeMashInput(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "팔로워 검색",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.sm),
        )

        val selectedPeople = followers.filter { it.id in selectedIds }
        if (selectedPeople.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.sm),
            ) {
                selectedPeople.forEach { person ->
                    Surface(
                        shape = RoundedCornerShape(LifeMashRadius.full),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { selectedIds = selectedIds - person.id }
                                .padding(horizontal = LifeMashSpacing.md, vertical = LifeMashSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.xxs),
                        ) {
                            Text(
                                text = person.nickname,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "팔로워 · ${followers.size}명",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.sm),
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            filtered.forEachIndexed { index, follower ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = LifeMashSpacing.xl),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                val isChecked = follower.id in selectedIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIds = if (isChecked) selectedIds - follower.id
                            else selectedIds + follower.id
                        }
                        .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LifeMashAvatar(name = follower.nickname)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = follower.nickname,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = follower.displayHandle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (isChecked) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isChecked) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(LifeMashSpacing.xxl))

            LifeMashButton(
                text = "${selectedIds.size}명에게 공개",
                onClick = { onConfirm(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LifeMashSpacing.xl),
            )

            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.size(LifeMashSpacing.lg))
        }
    }
}