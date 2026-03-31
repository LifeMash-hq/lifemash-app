package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.profile.UserProfileDto
import org.bmsk.lifemash.profile.ProfileRepository
import kotlin.uuid.Uuid

class FakeProfileRepository : ProfileRepository {
    data class ProfileData(var nickname: String, var bio: String?, var profileImage: String?, val email: String, val provider: String)

    private val profiles = mutableMapOf<Uuid, ProfileData>()
    private val followerCounts = mutableMapOf<Uuid, Int>()
    private val followingCounts = mutableMapOf<Uuid, Int>()

    fun addProfile(id: Uuid, nickname: String, email: String = "test@test.com", provider: String = "KAKAO", bio: String? = null) {
        profiles[id] = ProfileData(nickname, bio, null, email, provider)
    }

    fun setFollowerCount(userId: Uuid, count: Int) { followerCounts[userId] = count }
    fun setFollowingCount(userId: Uuid, count: Int) { followingCounts[userId] = count }

    override fun getProfile(userId: Uuid): UserProfileDto? {
        val p = profiles[userId] ?: return null
        return UserProfileDto(
            id = userId.toString(), email = p.email, nickname = p.nickname,
            bio = p.bio, profileImage = p.profileImage, provider = p.provider,
            followerCount = followerCounts[userId] ?: 0,
            followingCount = followingCounts[userId] ?: 0,
        )
    }

    override fun updateProfile(userId: Uuid, nickname: String?, bio: String?, profileImage: String?): UserProfileDto? {
        val p = profiles[userId] ?: return null
        if (nickname != null) p.nickname = nickname
        if (bio != null) p.bio = bio
        if (profileImage != null) p.profileImage = profileImage
        return getProfile(userId)
    }

    override fun getFollowerCount(userId: Uuid): Int = followerCounts[userId] ?: 0
    override fun getFollowingCount(userId: Uuid): Int = followingCounts[userId] ?: 0
}
