package org.bmsk.lifemash.calendar.impl

import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group

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
