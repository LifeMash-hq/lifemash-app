package org.bmsk.lifemash.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.usecase.calendar.GetMyGroupsUseCase
import org.bmsk.lifemash.domain.usecase.follow.FollowUserUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowersUseCase
import org.bmsk.lifemash.domain.usecase.follow.GetFollowingUseCase
import org.bmsk.lifemash.domain.usecase.follow.UnfollowUserUseCase

internal class FollowListViewModel(
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val uiState: StateFlow<FollowListUiState> = _uiState.asStateFlow()

    private val _followingState = MutableStateFlow<FollowListUiState>(FollowListUiState.Loading)
    val followingState: StateFlow<FollowListUiState> = _followingState.asStateFlow()

    private val _groupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val groupsState: StateFlow<GroupsUiState> = _groupsState.asStateFlow()

    fun loadFollowers(userId: String) {
        viewModelScope.launch {
            _uiState.value = FollowListUiState.Loading
            runCatching {
                getFollowersUseCase(userId)
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
                getFollowingUseCase(userId)
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
                getMyGroupsUseCase()
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
                if (willUnfollow) unfollowUserUseCase(userId)
                else followUserUseCase(userId)
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
                if (isCurrentlyFollowing) unfollowUserUseCase(userId)
                else followUserUseCase(userId)
            }
        }
    }
}
