package org.bmsk.lifemash.user

import org.bmsk.lifemash.model.auth.AuthUserDto
import kotlin.uuid.Uuid

interface UserRepository {
    fun upsert(email: String, provider: String, providerId: String, nickname: String, profileImage: String?): AuthUserDto
    fun findById(id: Uuid): AuthUserDto?
}
