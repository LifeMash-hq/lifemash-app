package org.bmsk.lifemash.profile

import org.bmsk.lifemash.fake.FakeProfileRepository
import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.plugins.BadRequestException
import kotlin.uuid.Uuid
import kotlin.test.*

class ProfileServiceTest {
    private fun createService(): Pair<ProfileService, FakeProfileRepository> {
        val repo = FakeProfileRepository()
        return ProfileService(repo) to repo
    }

    @Test
    fun `프로필_조회_시_팔로워_팔로잉_수가_포함된다`() {
        // Given
        val (service, repo) = createService()
        val userId = Uuid.random()
        repo.addProfile(userId, "테스트유저")
        repo.setFollowerCount(userId, 10)
        repo.setFollowingCount(userId, 5)

        // When
        val profile = service.getProfile(userId)

        // Then
        assertEquals(10, profile.followerCount)
        assertEquals(5, profile.followingCount)
    }

    @Test
    fun `내_프로필을_수정할_수_있다`() {
        // Given
        val (service, repo) = createService()
        val userId = Uuid.random()
        repo.addProfile(userId, "원래이름")

        // When
        val updated = service.updateProfile(userId, UpdateProfileRequest(nickname = "새이름", bio = "안녕"))

        // Then
        assertEquals("새이름", updated.nickname)
        assertEquals("안녕", updated.bio)
    }

    @Test
    fun `닉네임은_최대_12자이다`() {
        // Given
        val (service, repo) = createService()
        val userId = Uuid.random()
        repo.addProfile(userId, "유저")

        // When & Then
        assertFailsWith<BadRequestException> {
            service.updateProfile(userId, UpdateProfileRequest(nickname = "1234567890123"))
        }
    }
}
