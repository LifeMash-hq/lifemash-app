package org.bmsk.lifemash.profile

import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.model.profile.UserProfileDto
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

class ProfileService(
    private val profileRepository: ProfileRepository,
) {
    fun getProfile(userId: Uuid): UserProfileDto {
        return profileRepository.getProfile(userId)
            ?: throw NotFoundException("사용자를 찾을 수 없습니다")
    }

    fun updateProfile(userId: Uuid, request: UpdateProfileRequest): UserProfileDto {
        val nickname = request.nickname
        if (nickname != null && nickname.length > 12) {
            throw BadRequestException("닉네임은 최대 12자입니다")
        }
        return profileRepository.updateProfile(
            userId = userId,
            nickname = request.nickname,
            bio = request.bio,
            profileImage = request.profileImage,
        ) ?: throw NotFoundException("사용자를 찾을 수 없습니다")
    }
}
