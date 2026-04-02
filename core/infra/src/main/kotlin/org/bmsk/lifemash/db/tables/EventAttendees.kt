package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * event_attendees 테이블 — 일정 참가자 정보 기록.
 * 일정(Event)과 사용자(User)의 다대다 관계를 매핑합니다.
 */
object EventAttendees : Table("event_attendees") {
    val eventId = uuid("event_id").references(Events.id, onDelete = ReferenceOption.CASCADE)
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val joinedAt = timestampWithTimeZone("joined_at")

    override val primaryKey = PrimaryKey(eventId, userId)
}
