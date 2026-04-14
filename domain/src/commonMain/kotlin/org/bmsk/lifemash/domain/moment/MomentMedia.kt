package org.bmsk.lifemash.domain.moment

data class MomentMedia(
    val mediaUrl: String,
    val mediaType: MediaType,
    val sortOrder: Int,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
)

enum class MediaType(val value: String) {
    IMAGE("image"),
    VIDEO("video"),
}
