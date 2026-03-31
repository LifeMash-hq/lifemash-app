package org.bmsk.lifemash.social

import org.bmsk.lifemash.db.tables.Notifications
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.notification.NotificationDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class ExposedNotificationRepository : NotificationRepository {

    override fun create(
        userId: Uuid,
        type: String,
        actorId: Uuid?,
        targetId: Uuid?,
        content: String?,
    ): NotificationDto = transaction {
        val row = Notifications.insert {
            it[Notifications.userId] = userId.toJavaUuid()
            it[Notifications.type] = type
            it[Notifications.actorId] = actorId?.toJavaUuid()
            it[Notifications.targetId] = targetId?.toJavaUuid()
            it[Notifications.content] = content
            it[Notifications.isRead] = false
            it[Notifications.createdAt] = nowUtc()
        }.resultedValues!!.first()
        row.toDto(actorNickname = null, actorProfileImage = null)
    }

    override fun findByUser(userId: Uuid, limit: Int): List<NotificationDto> = transaction {
        Notifications
            .join(Users, JoinType.LEFT, Notifications.actorId, Users.id)
            .selectAll()
            .where { Notifications.userId eq userId.toJavaUuid() }
            .orderBy(Notifications.createdAt, SortOrder.DESC)
            .limit(limit)
            .map { row ->
                row.toDto(
                    actorNickname = row.getOrNull(Users.nickname),
                    actorProfileImage = row.getOrNull(Users.profileImage),
                )
            }
    }

    override fun markAsRead(notificationId: Uuid): Unit = transaction {
        Notifications.update({ Notifications.id eq notificationId.toJavaUuid() }) {
            it[isRead] = true
        }
    }

    override fun getUnreadCount(userId: Uuid): Int = transaction {
        Notifications.selectAll()
            .where { (Notifications.userId eq userId.toJavaUuid()) and (Notifications.isRead eq false) }
            .count()
            .toInt()
    }

    override fun deleteOldest(userId: Uuid, keepCount: Int): Unit = transaction {
        val keepIds = Notifications.selectAll()
            .where { Notifications.userId eq userId.toJavaUuid() }
            .orderBy(Notifications.createdAt, SortOrder.DESC)
            .limit(keepCount)
            .map { it[Notifications.id] }

        if (keepIds.size >= keepCount) {
            Notifications.deleteWhere {
                (Notifications.userId eq userId.toJavaUuid()) and
                    (Notifications.id notInList keepIds)
            }
        }
    }

    private fun ResultRow.toDto(actorNickname: String?, actorProfileImage: String?) = NotificationDto(
        id = this[Notifications.id].toString(),
        userId = this[Notifications.userId].toString(),
        type = this[Notifications.type],
        actorId = this[Notifications.actorId]?.toString(),
        actorNickname = actorNickname,
        actorProfileImage = actorProfileImage,
        targetId = this[Notifications.targetId]?.toString(),
        content = this[Notifications.content],
        isRead = this[Notifications.isRead],
        createdAt = this[Notifications.createdAt].toString(),
    )
}
