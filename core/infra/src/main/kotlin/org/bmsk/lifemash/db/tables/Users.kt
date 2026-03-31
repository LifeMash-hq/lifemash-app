package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * users 테이블 정의 — 소셜 로그인으로 가입한 사용자 정보를 저장.
 *
 * Exposed ORM에서 object로 테이블을 선언하면,
 * 각 val이 테이블의 컬럼(열)에 대응한다.
 *
 * uniqueIndex: 해당 컬럼(들)에 같은 값이 중복될 수 없음.
 * nullable(): NULL 허용 (값이 없을 수 있음).
 */
object Users : Table("users") {
    val id = uuid("id").autoGenerate()                         // 기본키, UUID 자동 생성
    val email = varchar("email", 255).uniqueIndex()            // 이메일 (중복 불가)
    val provider = varchar("provider", 20)                     // 로그인 제공자 (KAKAO, GOOGLE)
    val providerId = varchar("provider_id", 255)               // 소셜 서비스에서의 고유 ID
    val nickname = varchar("nickname", 50)                     // 표시 이름
    val profileImage = varchar("profile_image", 500).nullable() // 프로필 이미지 URL (없을 수 있음)
    val bio = varchar("bio", 300).nullable()                   // 자기소개 (없을 수 있음)
    val createdAt = timestampWithTimeZone("created_at")        // 가입 일시
    val updatedAt = timestampWithTimeZone("updated_at")        // 최종 수정 일시

    override val primaryKey = PrimaryKey(id)

    init {
        // 같은 소셜 서비스의 같은 사용자가 중복 가입되지 않도록 복합 유니크 인덱스
        uniqueIndex(provider, providerId)
    }
}
