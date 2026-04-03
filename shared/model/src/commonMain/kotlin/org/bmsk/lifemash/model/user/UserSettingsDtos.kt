package org.bmsk.lifemash.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    val startScreen: String,         // "moments" | "calendar"
    val viewStyleSelf: String,       // "dot" | "chip"
    val viewStyleOthers: String,     // "dot" | "chip"
    val defaultVisibility: String,   // "all" | "friends" | "followers" | "private"
)

@Serializable
data class UpdateSettingsRequest(
    val startScreen: String? = null,
    val viewStyleSelf: String? = null,
    val viewStyleOthers: String? = null,
    val defaultVisibility: String? = null,
)
