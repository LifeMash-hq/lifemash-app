package org.bmsk.lifemash.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileSettingsDto(
    val othersCalendarViewMode: String = "dot",
    val defaultEventVisibility: String = "public",
)

@Serializable
data class UpdateSettingsRequest(
    val othersCalendarViewMode: String? = null,
    val defaultEventVisibility: String? = null,
)
