package org.bmsk.lifemash.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import org.bmsk.lifemash.calendar.domain.model.Follower
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.FollowRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

sealed interface VisibilitySheetUiState {
    data object Loading : VisibilitySheetUiState
    data class Ready(
        val groups: List<Group>,
        val followers: List<Follower>,
        val groupCreation: GroupCreationState = GroupCreationState.Idle,
    ) : VisibilitySheetUiState
    data class Error(val message: String) : VisibilitySheetUiState
}

sealed interface GroupCreationState {
    data object Idle : GroupCreationState
    data object InProgress : GroupCreationState
    data class Failed(val message: String) : GroupCreationState
}

internal class VisibilitySheetViewModel(
    private val groupRepository: GroupRepository,
    private val followRepository: FollowRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VisibilitySheetUiState>(VisibilitySheetUiState.Loading)
    val uiState: StateFlow<VisibilitySheetUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        _uiState.value = VisibilitySheetUiState.Loading
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.getCurrentUser().first()?.id
                    ?: error("로그인 필요")
                val groupsDeferred = async { groupRepository.getMyGroups() }
                val followersDeferred = async { followRepository.getFollowers(userId) }
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
            runCatching { groupRepository.createGroup(type, name) }
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
