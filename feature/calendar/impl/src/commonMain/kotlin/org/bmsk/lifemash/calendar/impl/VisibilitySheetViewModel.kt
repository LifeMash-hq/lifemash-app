package org.bmsk.lifemash.calendar.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.usecase.auth.GetCurrentUserUseCase
import org.bmsk.lifemash.domain.usecase.calendar.CreateGroupUseCase
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowersUseCase

internal class VisibilitySheetViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VisibilitySheetUiState>(VisibilitySheetUiState.Loading)
    val uiState: StateFlow<VisibilitySheetUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        _uiState.value = VisibilitySheetUiState.Loading
        viewModelScope.launch {
            runCatching {
                val userId = getCurrentUserUseCase().first()?.id
                    ?: error("로그인 필요")
                val groupsDeferred = async { getMyGroupsUseCase() }
                val followersDeferred = async { getFollowersUseCase(userId) }
                VisibilitySheetUiState.Ready(
                    groups = groupsDeferred.await(),
                    followers = followersDeferred.await(),
                )
            }.fold(
                onSuccess = { _uiState.value = it },
                onFailure = { _uiState.value = VisibilitySheetUiState.Error(it.message ?: "로딩 실패") },
            )
        }
    }

    fun createGroup(name: String, type: GroupType) {
        val current = _uiState.value as? VisibilitySheetUiState.Ready ?: return
        _uiState.value = current.copy(groupCreation = GroupCreationState.InProgress)
        viewModelScope.launch {
            runCatching { createGroupUseCase(type, name) }
                .fold(
                    onSuccess = { load() },
                    onFailure = { e ->
                        _uiState.value = current.copy(
                            groupCreation = GroupCreationState.Failed(e.message ?: "그룹 생성 실패"),
                        )
                    },
                )
        }
    }
}
