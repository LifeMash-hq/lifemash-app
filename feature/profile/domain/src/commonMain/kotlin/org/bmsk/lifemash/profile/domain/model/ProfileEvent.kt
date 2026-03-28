package org.bmsk.lifemash.profile.domain.model

data class ProfileEvent(
    val id: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val color: String = "#4F6AF5",
    val visibility: String = "public",
)
