package org.bmsk.lifemash.model.moment

import kotlinx.serialization.Serializable

@Serializable
data class MomentDto(
    val id: String,
    val eventId: String,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val imageUrl: String,
    val caption: String? = null,
    val visibility: String = "public",
    val createdAt: String,
)

@Serializable
data class CreateMomentRequest(
    val imageUrl: String,
    val caption: String? = null,
    val visibility: String = "public",
)
