package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * groups 테이블 정의 — 사용자들이 속하는 그룹(커플, 가족 등) 정보.
 * 초대 코드를 통해 다른 사용자가 그룹에 참여할 수 있다.
 */
object Groups : Table("groups") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 100).nullable()                  // 그룹 이름 (선택사항)
    val type = varchar("type", 20).default("COUPLE")            // 그룹 유형: COUPLE(기본), FAMILY 등
    val maxMembers = integer("max_members").default(2)          // 최대 멤버 수 (커플=2, 기타=50)
    val inviteCode = varchar("invite_code", 8).uniqueIndex()   // 8자리 초대 코드 (중복 불가)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}
