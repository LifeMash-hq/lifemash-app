package org.bmsk.lifemash.user

import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.model.user.UserSettingsDto
import kotlin.uuid.Uuid

interface UserRepository {
    fun upsert(email: String, provider: String, providerId: String, nickname: String, profileImage: String?): AuthUserDto
    fun findById(id: Uuid): AuthUserDto?
    fun findByEmail(email: String): AuthUserDto?
    fun getPasswordHash(email: String): String?
    fun upsertEmailUser(email: String, passwordHash: String, nickname: String): AuthUserDto
    fun getSettings(userId: Uuid): UserSettingsDto?
    fun updateSettings(
        userId: Uuid,
        startScreen: String?,
        viewStyleSelf: String?,
        viewStyleOthers: String?,
        defaultVisibility: String?,
    ): UserSettingsDto?
}
