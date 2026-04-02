package org.bmsk.lifemash.profile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileMomentMedia(
    val mediaUrl: String,
    val mediaType: String,
    val sortOrder: Int,
)

@Serializable
data class Moment(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val authorId: String,
    val authorNickname: String,
    val media: List<ProfileMomentMedia> = emptyList(),
    val caption: String? = null,
    val visibility: String = "public",
    val createdAt: String,
)
