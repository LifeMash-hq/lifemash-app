package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.GroupMemberDto
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.bmsk.lifemash.db.tables.GroupMembers
import org.bmsk.lifemash.db.tables.Groups
import org.bmsk.lifemash.db.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class ExposedGroupRepository : GroupRepository {

    override fun create(userId: Uuid, type: String, name: String?): GroupDto = transaction {
        val now = now()
        val maxMembers = when (type) {
            "COUPLE" -> 2
            else -> 50
        }
        val inviteCode = generateInviteCode()

        val groupId = Groups.insert {
            it[Groups.name] = name
            it[Groups.type] = type
            it[Groups.maxMembers] = maxMembers
            it[Groups.inviteCode] = inviteCode
            it[Groups.createdAt] = now
        }[Groups.id]

        GroupMembers.insert {
            it[GroupMembers.groupId] = groupId
            it[GroupMembers.userId] = userId.toJavaUuid()
            it[GroupMembers.role] = "OWNER"
            it[GroupMembers.joinedAt] = now
        }

        findByIdInternal(groupId.toKotlinUuid())!!
    }

    override fun join(userId: Uuid, inviteCode: String): GroupDto = transaction {
        val group = Groups.selectAll().where { Groups.inviteCode eq inviteCode }.singleOrNull()
            ?: throw org.bmsk.lifemash.plugins.NotFoundException("Group not found")

        val groupId = group[Groups.id]
        val memberCount = GroupMembers.selectAll().where { GroupMembers.groupId eq groupId }.count()

        if (memberCount >= group[Groups.maxMembers]) {
            throw org.bmsk.lifemash.plugins.ForbiddenException("Group is full")
        }

        val existing = GroupMembers.selectAll().where {
            (GroupMembers.groupId eq groupId) and (GroupMembers.userId eq userId.toJavaUuid())
        }.singleOrNull()

        if (existing == null) {
            GroupMembers.insert {
                it[GroupMembers.groupId] = groupId
                it[GroupMembers.userId] = userId.toJavaUuid()
                it[GroupMembers.role] = "MEMBER"
                it[GroupMembers.joinedAt] = now()
            }
        }

        findByIdInternal(groupId.toKotlinUuid())!!
    }

    override fun findByUserId(userId: Uuid): List<GroupDto> = transaction {
        val groupIds = GroupMembers.selectAll()
            .where { GroupMembers.userId eq userId.toJavaUuid() }
            .map { it[GroupMembers.groupId].toKotlinUuid() }

        groupIds.mapNotNull { findByIdInternal(it) }
    }

    override fun findById(groupId: Uuid): GroupDto? = transaction {
        findByIdInternal(groupId)
    }

    private fun findByIdInternal(groupId: Uuid): GroupDto? {
        val group = Groups.selectAll().where { Groups.id eq groupId.toJavaUuid() }.singleOrNull() ?: return null

        val members = (GroupMembers innerJoin Users)
            .selectAll().where { GroupMembers.groupId eq groupId.toJavaUuid() }
            .map {
                GroupMemberDto(
                    userId = it[GroupMembers.userId].toString(),
                    nickname = it[Users.nickname],
                    profileImage = it[Users.profileImage],
                    role = it[GroupMembers.role],
                    joinedAt = it[GroupMembers.joinedAt].toKotlinxInstant(),
                )
            }

        return GroupDto(
            id = group[Groups.id].toString(),
            name = group[Groups.name],
            type = group[Groups.type],
            maxMembers = group[Groups.maxMembers],
            inviteCode = group[Groups.inviteCode],
            members = members,
            createdAt = group[Groups.createdAt].toKotlinxInstant(),
        )
    }

    override fun delete(groupId: Uuid, userId: Uuid) = transaction {
        val member = GroupMembers.selectAll().where {
            (GroupMembers.groupId eq groupId.toJavaUuid()) and (GroupMembers.userId eq userId.toJavaUuid())
        }.singleOrNull() ?: throw org.bmsk.lifemash.plugins.ForbiddenException("Not a member")

        if (member[GroupMembers.role] != "OWNER") {
            throw org.bmsk.lifemash.plugins.ForbiddenException("Only OWNER can delete group")
        }

        Groups.deleteWhere { Groups.id eq groupId.toJavaUuid() }
        Unit
    }

    override fun isMember(groupId: Uuid, userId: Uuid): Boolean = transaction {
        GroupMembers.selectAll().where {
            (GroupMembers.groupId eq groupId.toJavaUuid()) and (GroupMembers.userId eq userId.toJavaUuid())
        }.singleOrNull() != null
    }

    override fun getMemberUserIds(groupId: Uuid): List<Uuid> = transaction {
        GroupMembers.selectAll().where { GroupMembers.groupId eq groupId.toJavaUuid() }
            .map { it[GroupMembers.userId].toKotlinUuid() }
    }

    override fun updateName(groupId: Uuid, name: String): GroupDto = transaction {
        Groups.update({ Groups.id eq groupId.toJavaUuid() }) {
            it[Groups.name] = name
        }
        findByIdInternal(groupId) ?: throw org.bmsk.lifemash.plugins.NotFoundException("그룹을 찾을 수 없습니다")
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }

    private fun now(): OffsetDateTime =
        nowUtc()
}
