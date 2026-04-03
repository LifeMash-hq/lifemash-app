package org.bmsk.lifemash.calendar.domain.model

sealed interface EventVisibility {
    data object Public : EventVisibility
    data object Followers : EventVisibility
    data class Group(val groupId: String) : EventVisibility
    data class Specific(val userIds: List<String>) : EventVisibility
    data object Private : EventVisibility
}

fun EventVisibility.toRouteString(): String = when (this) {
    EventVisibility.Public -> "public"
    EventVisibility.Followers -> "followers"
    is EventVisibility.Group -> "group:$groupId"
    is EventVisibility.Specific -> "specific:${userIds.joinToString(",")}"
    EventVisibility.Private -> "private"
}

fun parseEventVisibility(value: String): EventVisibility = when {
    value == "public" -> EventVisibility.Public
    value == "followers" -> EventVisibility.Followers
    value.startsWith("group:") -> EventVisibility.Group(value.removePrefix("group:"))
    value.startsWith("specific:") -> {
        val ids = value.removePrefix("specific:").split(",").filter { it.isNotBlank() }
        EventVisibility.Specific(ids)
    }
    value == "private" -> EventVisibility.Private
    else -> EventVisibility.Followers
}
