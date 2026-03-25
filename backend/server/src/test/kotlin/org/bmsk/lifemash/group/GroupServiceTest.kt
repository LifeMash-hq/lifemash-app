package org.bmsk.lifemash.group

import org.bmsk.lifemash.fake.FakeGroupRepository
import org.bmsk.lifemash.model.calendar.CreateGroupRequest
import org.bmsk.lifemash.model.calendar.JoinGroupRequest
import org.bmsk.lifemash.model.calendar.UpdateGroupRequest
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import java.util.*
import kotlin.test.*

class GroupServiceTest {

    private lateinit var groupRepo: FakeGroupRepository
    private lateinit var service: GroupService

    private val userId = UUID.randomUUID()

    @BeforeTest
    fun setUp() {
        groupRepo = FakeGroupRepository()
        service = GroupServiceImpl(groupRepo)
    }

    @Test
    fun `그룹을 생성하면 OWNER로 자동 가입된다`() {
        // When
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE", name = "우리 커플"))

        // Then
        assertEquals(1, group.members.size)
        assertEquals("OWNER", group.members[0].role)
        assertEquals(userId.toString(), group.members[0].userId)
    }

    @Test
    fun `커플 그룹의 최대 인원은 2명이다`() {
        // When
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))

        // Then
        assertEquals(2, group.maxMembers)
    }

    @Test
    fun `일반 그룹의 최대 인원은 50명이다`() {
        // When
        val group = service.create(userId, CreateGroupRequest(type = "FAMILY"))

        // Then
        assertEquals(50, group.maxMembers)
    }

    @Test
    fun `초대 코드로 그룹에 참여한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))
        val otherUser = UUID.randomUUID()

        // When
        val joined = service.join(otherUser, JoinGroupRequest(group.inviteCode))

        // Then
        assertEquals(2, joined.members.size)
    }

    @Test
    fun `정원이 찬 그룹에 참여 시 ForbiddenException이 발생한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))
        service.join(UUID.randomUUID(), JoinGroupRequest(group.inviteCode))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.join(UUID.randomUUID(), JoinGroupRequest(group.inviteCode))
        }
    }

    @Test
    fun `이미 가입한 그룹에 다시 참여해도 중복되지 않는다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))

        // When
        val rejoined = service.join(userId, JoinGroupRequest(group.inviteCode))

        // Then
        assertEquals(1, rejoined.members.size)
    }

    @Test
    fun `OWNER가 그룹을 삭제한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))

        // When
        service.delete(UUID.fromString(group.id), userId)

        // Then
        assertFailsWith<NotFoundException> {
            service.getGroup(UUID.fromString(group.id))
        }
    }

    @Test
    fun `MEMBER가 그룹 삭제 시 ForbiddenException이 발생한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))
        val memberId = UUID.randomUUID()
        service.join(memberId, JoinGroupRequest(group.inviteCode))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.delete(UUID.fromString(group.id), memberId)
        }
    }

    @Test
    fun `내 그룹 목록을 조회한다`() {
        // Given
        service.create(userId, CreateGroupRequest(type = "COUPLE", name = "그룹1"))
        service.create(userId, CreateGroupRequest(type = "FAMILY", name = "그룹2"))

        // When
        val groups = service.getMyGroups(userId)

        // Then
        assertEquals(2, groups.size)
    }

    @Test
    fun `존재하지 않는 그룹 조회 시 NotFoundException이 발생한다`() {
        // When & Then
        assertFailsWith<NotFoundException> {
            service.getGroup(UUID.randomUUID())
        }
    }

    @Test
    fun `OWNER가 그룹명을 변경한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE", name = "기존 이름"))

        // When
        val updated = service.updateName(UUID.fromString(group.id), userId, UpdateGroupRequest("새 이름"))

        // Then
        assertEquals("새 이름", updated.name)
    }

    @Test
    fun `MEMBER가 그룹명 변경 시 ForbiddenException이 발생한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))
        val memberId = UUID.randomUUID()
        service.join(memberId, JoinGroupRequest(group.inviteCode))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.updateName(UUID.fromString(group.id), memberId, UpdateGroupRequest("새 이름"))
        }
    }

    @Test
    fun `공백 그룹명으로 변경 시 IllegalArgumentException이 발생한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            service.updateName(UUID.fromString(group.id), userId, UpdateGroupRequest("   "))
        }
    }

    @Test
    fun `20자 초과 그룹명으로 변경 시 IllegalArgumentException이 발생한다`() {
        // Given
        val group = service.create(userId, CreateGroupRequest(type = "COUPLE"))

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            service.updateName(UUID.fromString(group.id), userId, UpdateGroupRequest("a".repeat(21)))
        }
    }
}
