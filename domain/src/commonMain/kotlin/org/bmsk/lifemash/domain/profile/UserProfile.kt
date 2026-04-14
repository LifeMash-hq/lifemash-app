package org.bmsk.lifemash.domain.profile

data class UserProfile(
    val id: String,
    val email: String,
    val nickname: String,
    val username: String? = null,
    val bio: String? = null,
    val profileImage: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
)
