package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.user.UserRepository
import java.util.*

class FakeUserRepository : UserRepository {
    private val users = mutableMapOf<UUID, AuthUserDto>()
    private val providerIndex = mutableMapOf<String, UUID>() // "PROVIDER:providerId" -> userId

    override fun upsert(email: String, provider: String, providerId: String, nickname: String, profileImage: String?): AuthUserDto {
        val key = "$provider:$providerId"
        val existingId = providerIndex[key]
        return if (existingId != null) {
            val updated = users[existingId]!!.copy(nickname = nickname, profileImage = profileImage)
            users[existingId] = updated
            updated
        } else {
            val id = UUID.randomUUID()
            val user = AuthUserDto(id = id.toString(), email = email, nickname = nickname, profileImage = profileImage, provider = provider)
            users[id] = user
            providerIndex[key] = id
            user
        }
    }

    override fun findById(id: UUID): AuthUserDto? = users[id]
}
