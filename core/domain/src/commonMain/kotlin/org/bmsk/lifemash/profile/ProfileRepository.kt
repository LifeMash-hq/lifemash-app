package org.bmsk.lifemash.profile

import org.bmsk.lifemash.model.profile.UserProfileDto
import kotlin.uuid.Uuid

interface ProfileRepository {
    fun getProfile(userId: Uuid): UserProfileDto?
    fun updateProfile(userId: Uuid, nickname: String?, bio: String?, profileImage: String?): UserProfileDto?
    fun getFollowerCount(userId: Uuid): Int
    fun getFollowingCount(userId: Uuid): Int
}
