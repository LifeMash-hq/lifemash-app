package org.bmsk.lifemash.profile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Moment(
    val id: String,
    val eventId: String,
    val authorId: String,
    val authorNickname: String,
    val imageUrl: String,
    val caption: String? = null,
    val visibility: String = "public",
    val createdAt: String,
)
