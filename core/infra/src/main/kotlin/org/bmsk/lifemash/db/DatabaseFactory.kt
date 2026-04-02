package org.bmsk.lifemash.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bmsk.lifemash.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 데이터베이스 연결 및 테이블 초기화를 담당하는 팩토리.
 *
 * 사용하는 기술:
 * - PostgreSQL: 관계형 데이터베이스 (데이터를 테이블/행/열로 저장)
 * - HikariCP: DB 커넥션 풀 — 매번 연결을 새로 만들지 않고 미리 연결을 만들어 재사용 (성능 최적화)
 * - Exposed: Kotlin용 ORM — SQL을 직접 쓰지 않고 Kotlin 코드로 DB를 조작
 */
object DatabaseFactory {
    fun init() {
        // 환경변수에서 DB 접속 URL을 가져옴 (예: jdbc:postgresql://host:5432/dbname?user=...&password=...)
        val databaseUrl = org.bmsk.lifemash.config.EnvConfig.require("DATABASE_URL")

        // 커넥션 풀 설정
        val config = HikariConfig().apply {
            jdbcUrl = databaseUrl
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5  // 동시에 유지할 최대 DB 연결 수
            isAutoCommit = false // 트랜잭션을 명시적으로 커밋해야 함 (데이터 일관성 보장)
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"  // 트랜잭션 격리 수준: 같은 트랜잭션 내에서 반복 읽기 시 동일 결과 보장
            validate()
        }

        Database.connect(HikariDataSource(config))

        // 서버 시작 시 테이블/컬럼이 없으면 자동 생성 (이미 있으면 무시, 누락 컬럼은 추가)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users, Groups, GroupMembers, Events, Comments, EventAttendees,
                AssistantConversations, AssistantMessages, AssistantUsage, UserApiKeys,
                Follows, Moments, MomentMedia, Likes, Notifications,
                Memos, ChecklistItems,
            )
        }
    }
}
