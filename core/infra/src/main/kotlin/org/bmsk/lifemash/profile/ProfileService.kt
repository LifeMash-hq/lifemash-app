package org.bmsk.lifemash.profile

import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.model.profile.UserProfileDto
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

class ProfileService(
    private val profileRepository: ProfileRepository,
) {
    fun getProfile(userId: Uuid, viewerId: Uuid? = null): UserProfileDto {
        return profileRepository.getProfile(userId, viewerId)
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

    fun checkHandleAvailability(handle: String): Boolean {
        val regex = "^[a-z0-9_]{3,15}$".toRegex()
        if (!regex.matches(handle)) {
            throw BadRequestException("아이디는 영소문자, 숫자, 밑줄(_) 3~15자로 입력해주세요")
        }
        return profileRepository.checkHandleAvailability(handle)
    }
}
