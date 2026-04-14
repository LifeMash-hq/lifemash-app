package org.bmsk.lifemash.domain.calendar

data class Follower(
    val id: String,
    val nickname: String,
    val profileImage: String?,
) {
    val displayHandle: String get() = "@$nickname"

    fun matchesQuery(query: String): Boolean =
        query.isBlank() || nickname.contains(query, ignoreCase = true)
}
