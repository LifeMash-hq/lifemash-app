package org.bmsk.lifemash.profile.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.designsystem.component.AvatarSize
import org.bmsk.lifemash.designsystem.component.LifeMashAvatar
import org.bmsk.lifemash.designsystem.component.LifeMashButton
import org.bmsk.lifemash.designsystem.component.LifeMashButtonSize
import org.bmsk.lifemash.designsystem.component.LifeMashButtonStyle
import org.bmsk.lifemash.designsystem.component.LifeMashInput
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FollowListSheet(
    profileUserId: String,
    onDismiss: () -> Unit,
    onUserClick: (String) -> Unit = {},
    onCreateGroup: () -> Unit = {},
    viewModel: FollowListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val followingState by viewModel.followingState.collectAsStateWithLifecycle()
    val groupsState by viewModel.groupsState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(profileUserId) {
        viewModel.loadFollowers(profileUserId)
        viewModel.loadFollowing(profileUserId)
        viewModel.loadGroups()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "팔로워 / 팔로잉",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.md),
            )

            SecondaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("팔로워") },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("팔로잉") },
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("그룹") },
                )
            }

            if (selectedTab == 0) {
                // 팔로워 탭 — 실제 데이터
                Column(modifier = Modifier.fillMaxWidth().padding(top = LifeMashSpacing.md)) {
                    val loaded = uiState as? FollowListUiState.Loaded

                    LifeMashInput(
                        value = loaded?.query ?: "",
                        onValueChange = viewModel::updateQuery,
                        placeholder = "검색",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = LifeMashSpacing.xl),
                    )

                    Spacer(Modifier.height(LifeMashSpacing.md))

                    when (val state = uiState) {
                        is FollowListUiState.Loading -> {
                            Box(
                                Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                                contentAlignment = Alignment.Center,
                            ) { CircularProgressIndicator() }
                        }
                        is FollowListUiState.Error -> {
                            Box(
                                Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                                contentAlignment = Alignment.Center,
                            ) { Text(state.message) }
                        }
                        is FollowListUiState.Loaded -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.xs),
                            ) {
                                items(state.filtered, key = { it.id }) { follower ->
                                    FollowerRow(
                                        nickname = follower.nickname,
                                        profileImage = follower.profileImage,
                                        onUserClick = { onUserClick(follower.id) },
                                    )
                                }
                                item { Spacer(Modifier.height(LifeMashSpacing.xxxl)) }
                            }
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // 팔로잉 탭
                Column(modifier = Modifier.fillMaxWidth().padding(top = LifeMashSpacing.md)) {
                    val loadedFollowing = followingState as? FollowListUiState.Loaded

                    LifeMashInput(
                        value = loadedFollowing?.query ?: "",
                        onValueChange = viewModel::updateFollowingQuery,
                        placeholder = "검색",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = LifeMashSpacing.xl),
                    )

                    Spacer(Modifier.height(LifeMashSpacing.md))

                    when (val state = followingState) {
                        is FollowListUiState.Loading -> {
                            Box(
                                Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                                contentAlignment = Alignment.Center,
                            ) { CircularProgressIndicator() }
                        }
                        is FollowListUiState.Error -> {
                            Box(
                                Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                                contentAlignment = Alignment.Center,
                            ) { Text(state.message) }
                        }
                        is FollowListUiState.Loaded -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.xs),
                            ) {
                                items(state.filtered, key = { it.id }) { following ->
                                    FollowingPersonRow(
                                        nickname = following.nickname,
                                        profileImage = following.profileImage,
                                        isFollowing = following.id !in state.unfollowedIds,
                                        onToggleFollow = { viewModel.toggleFollowInFollowing(following.id) },
                                        onUserClick = { onUserClick(following.id) },
                                    )
                                }
                                item { Spacer(Modifier.height(LifeMashSpacing.xxxl)) }
                            }
                        }
                    }
                }
            } else {
                // 그룹 탭
                when (val state = groupsState) {
                    is GroupsUiState.Loading -> {
                        Box(
                            Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                    }
                    is GroupsUiState.Error -> {
                        Box(
                            Modifier.fillMaxWidth().height(LifeMashSpacing.xxxl * 3),
                            contentAlignment = Alignment.Center,
                        ) { Text(state.message) }
                    }
                    is GroupsUiState.Loaded -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().padding(top = LifeMashSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(LifeMashSpacing.xs),
                        ) {
                            item {
                                Text(
                                    text = "내 그룹 ${state.groups.size}개",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(
                                        horizontal = LifeMashSpacing.xl,
                                        vertical = LifeMashSpacing.xs,
                                    ),
                                )
                            }
                            items(state.groups, key = { it.id }) { group ->
                                GroupRow(group = group)
                            }
                            item { NewGroupRow(onClick = onCreateGroup) }
                            item { Spacer(Modifier.height(LifeMashSpacing.xxxl)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowerRow(
    nickname: String,
    profileImage: String?,
    onUserClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LifeMashAvatar(
            imageUrl = profileImage,
            name = nickname,
            size = AvatarSize.Medium,
        )
        Spacer(Modifier.width(LifeMashSpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "@$nickname",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LifeMashButton(
            text = "프로필",
            onClick = onUserClick,
            style = LifeMashButtonStyle.Ghost,
            size = LifeMashButtonSize.Small,
        )
    }
}

@Composable
private fun GroupRow(group: Group) {
    val typeLabel = when (group.type) {
        GroupType.COUPLE -> "커플"
        GroupType.FAMILY -> "가족"
        GroupType.FRIENDS -> "친구"
        GroupType.TEAM -> "팀"
    }
    val typeIcon = when (group.type) {
        GroupType.COUPLE -> "💑"
        GroupType.FAMILY -> "👨‍👩‍👧‍👦"
        GroupType.FRIENDS -> "👫"
        GroupType.TEAM -> "💼"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(LifeMashSpacing.xxxl)
                .clip(RoundedCornerShape(LifeMashRadius.md))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = typeIcon, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.width(LifeMashSpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.name ?: typeLabel,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "멤버 ${group.members.size}명 · $typeLabel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(LifeMashSpacing.xl),
        )
    }
}

@Composable
private fun FollowingPersonRow(
    nickname: String,
    profileImage: String?,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
    onUserClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LifeMashAvatar(
            imageUrl = profileImage,
            name = nickname,
            size = AvatarSize.Medium,
        )
        Spacer(Modifier.width(LifeMashSpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "@$nickname",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LifeMashButton(
            text = if (isFollowing) "팔로잉" else "팔로우",
            onClick = onToggleFollow,
            style = if (isFollowing) LifeMashButtonStyle.Secondary else LifeMashButtonStyle.Primary,
            size = LifeMashButtonSize.Small,
        )
    }
}

@Composable
private fun NewGroupRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = LifeMashSpacing.xl, vertical = LifeMashSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LifeMashSpacing.md),
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(LifeMashSpacing.xxxl),
        )
        Text(
            text = "새 그룹 만들기",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
