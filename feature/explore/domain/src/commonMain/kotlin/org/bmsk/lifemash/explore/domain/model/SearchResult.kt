package org.bmsk.lifemash.explore.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSummary(val id: String, val nickname: String, val profileImage: String? = null)

@Serializable
data class EventSummary(val id: String, val title: String, val startAt: String, val color: String? = null)
