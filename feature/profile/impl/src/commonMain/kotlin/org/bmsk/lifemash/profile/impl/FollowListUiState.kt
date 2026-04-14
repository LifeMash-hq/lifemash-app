package org.bmsk.lifemash.profile.impl

import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group

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
