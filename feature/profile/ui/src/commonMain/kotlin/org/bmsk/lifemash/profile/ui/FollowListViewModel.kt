package org.bmsk.lifemash.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.calendar.domain.model.Follower
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.FollowRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

sealed interface FollowListUiState {
    data object Loading : FollowListUiState
    data class Loaded(
        val followers: List<Follower>,
        val query: String = "",
        val unfollowedIds: Set<String> = emptySet(),
    ) : FollowListUiState {
        val filtered: List<Follower> get() = followers.filter { it.matchesQuery(query) }
    }
    data class Error(val message: String) : FollowListUiState
}

sealed interface GroupsUiState {
    data object Loading : GroupsUiState
    data class Loaded(val groups: List<Group>) : GroupsUiState
    data class Error(val message: String) : GroupsUiState
}

internal class FollowListViewModel(
    private val followRepository: FollowRepository,
    private val profileRepository: ProfileRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val uiState: StateFlow<FollowListUiState> = _uiState

    private val _followingState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val followingState: StateFlow<FollowListUiState> = _followingState

    private val _groupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val groupsState: StateFlow<GroupsUiState> = _groupsState

    fun loadFollowers(userId: String) {
        viewModelScope.launch {
            _uiState.value = FollowListUiState.Loading
            runCatching {
                followRepository.getFollowers(userId)
            }.onSuccess { followers ->
                _uiState.value = FollowListUiState.Loaded(followers = followers)
            }.onFailure { e ->
                _uiState.value = FollowListUiState.Error(e.message ?: "오류")
            }
        }
    }

    fun loadFollowing(userId: String) {
        viewModelScope.launch {
            _followingState.value = FollowListUiState.Loading
            runCatching {
                followRepository.getFollowing(userId)
            }.onSuccess { following ->
                _followingState.value = FollowListUiState.Loaded(followers = following)
            }.onFailure { e ->
                _followingState.value = FollowListUiState.Error(e.message ?: "오류")
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { state ->
            if (state is FollowListUiState.Loaded) state.copy(query = query) else state
        }
    }

    fun updateFollowingQuery(query: String) {
        _followingState.update { state ->
            if (state is FollowListUiState.Loaded) state.copy(query = query) else state
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            _groupsState.value = GroupsUiState.Loading
            runCatching {
                groupRepository.getMyGroups()
            }.onSuccess { groups ->
                _groupsState.value = GroupsUiState.Loaded(groups = groups)
            }.onFailure { e ->
                _groupsState.value = GroupsUiState.Error(e.message ?: "오류")
            }
        }
    }

    fun toggleFollowInFollowing(userId: String) {
        val current = _followingState.value as? FollowListUiState.Loaded ?: return
        val willUnfollow = userId !in current.unfollowedIds
        _followingState.update {
            if (it is FollowListUiState.Loaded)
                it.copy(unfollowedIds = if (willUnfollow) it.unfollowedIds + userId else it.unfollowedIds - userId)
            else it
        }
        viewModelScope.launch {
            runCatching {
                if (willUnfollow) profileRepository.unfollow(userId)
                else profileRepository.follow(userId)
            }.onFailure {
                _followingState.update { state ->
                    if (state is FollowListUiState.Loaded)
                        state.copy(unfollowedIds = if (willUnfollow) state.unfollowedIds - userId else state.unfollowedIds + userId)
                    else state
                }
            }
        }
    }

    fun toggleFollow(userId: String, isCurrentlyFollowing: Boolean) {
        viewModelScope.launch {
            runCatching {
                if (isCurrentlyFollowing) profileRepository.unfollow(userId)
                else profileRepository.follow(userId)
            }
        }
    }
}
