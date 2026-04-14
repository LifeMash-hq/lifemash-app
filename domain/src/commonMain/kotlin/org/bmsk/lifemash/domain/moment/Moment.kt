package org.bmsk.lifemash.domain.moment

data class Moment(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val authorId: String,
    val authorNickname: String,
    val authorProfileImage: String? = null,
    val caption: String? = null,
    val visibility: Visibility,
    val media: List<MomentMedia>,
    val createdAt: String,
)
