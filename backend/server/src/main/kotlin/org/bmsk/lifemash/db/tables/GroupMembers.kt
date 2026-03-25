package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * group_members 테이블 — 그룹과 사용자 간의 다대다(N:N) 관계를 연결하는 "중간 테이블".
 *
 * 한 사용자가 여러 그룹에 속할 수 있고, 한 그룹에 여러 사용자가 있을 수 있다.
 * 기본키가 (groupId, userId) 복합키 → 같은 그룹에 같은 사용자가 중복 가입 불가.
 *
 * references: 외래키(FK) — 다른 테이블의 행을 참조
 * CASCADE: 부모(그룹/사용자)가 삭제되면 이 테이블의 관련 행도 함께 삭제
 */
object GroupMembers : Table("group_members") {
    val groupId = uuid("group_id").references(Groups.id, onDelete = ReferenceOption.CASCADE)
    val userId = uuid("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20).default("MEMBER")  // 역할: OWNER(생성자) 또는 MEMBER
    val joinedAt = timestampWithTimeZone("joined_at")

    override val primaryKey = PrimaryKey(groupId, userId)

    init {
        // userId로 조회할 때 빠르게 찾기 위한 인덱스 (사용자의 그룹 목록 조회 최적화)
        index(false, userId)
    }
}
