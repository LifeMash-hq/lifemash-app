package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * events 테이블 — 그룹 내 캘린더 일정 정보.
 * 각 일정은 반드시 하나의 그룹에 속하며, 작성자(authorId)가 있다.
 */
object Events : Table("events") {
    val id = uuid("id").autoGenerate()
    val groupId = uuid("group_id").references(Groups.id, onDelete = ReferenceOption.CASCADE) // 소속 그룹
    val authorId = uuid("author_id").references(Users.id)   // 일정 작성자
    val title = varchar("title", 200)                        // 일정 제목
    val description = text("description").nullable()         // 일정 상세 설명 (선택)
    val startAt = timestampWithTimeZone("start_at")          // 시작 일시
    val endAt = timestampWithTimeZone("end_at").nullable()   // 종료 일시 (선택, 종일 일정 등)
    val isAllDay = bool("is_all_day").default(false)         // 종일 일정 여부
    val color = varchar("color", 7).nullable()               // 일정 색상 (#FF0000 등, 선택)
    val location = varchar("location", 500).nullable()       // 장소 정보 (선택)
    val imageEmoji = varchar("image_emoji", 10).nullable()   // 일정 대표 이모지 (선택)
    val visibility = varchar("visibility", 20).default("followers") // 공개 범위
    val visibilityGroupId = uuid("visibility_group_id").nullable()  // 그룹 공개 시 대상 그룹 ID
    val visibilityUserIds = text("visibility_user_ids").nullable()  // 특정인 공개 시 유저 ID 목록 (쉼표 구분)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        // (그룹ID + 시작일) 복합 인덱스: 특정 그룹의 월별 일정 조회를 빠르게 하기 위함
        index(false, groupId, startAt)
    }
}
