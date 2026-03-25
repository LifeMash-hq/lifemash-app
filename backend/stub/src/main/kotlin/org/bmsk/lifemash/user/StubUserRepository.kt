package org.bmsk.lifemash.user

import org.bmsk.lifemash.model.auth.AuthUserDto
import java.util.*

class StubUserRepository : UserRepository {
    private val users = mutableMapOf<UUID, AuthUserDto>()

    override fun upsert(
        email: String,
        provider: String,
        providerId: String,
        nickname: String,
        profileImage: String?,
    ): AuthUserDto {
        val existing = users.values.find { it.email == email && it.provider == provider }
        if (existing != null) return existing

        val id = UUID.randomUUID()
        val user = AuthUserDto(
            id = id.toString(),
            email = email,
            nickname = nickname,
            profileImage = profileImage,
            provider = provider,
        )
        users[id] = user
        return user
    }

    override fun findById(id: UUID): AuthUserDto? =
        users[id]
}
