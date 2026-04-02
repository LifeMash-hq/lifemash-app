package org.bmsk.lifemash.calendar.domain.model

sealed interface EventVisibility {
    data object Public : EventVisibility
    data object Followers : EventVisibility
    data class Group(val groupId: String) : EventVisibility
    data class Specific(val userIds: List<String>) : EventVisibility
    data object Private : EventVisibility
}
