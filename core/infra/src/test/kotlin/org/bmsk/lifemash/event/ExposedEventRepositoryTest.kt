@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.event

import org.bmsk.lifemash.db.tables.EventAttendees
import org.bmsk.lifemash.db.tables.Events
import org.bmsk.lifemash.db.tables.Groups
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.db.tables.Comments
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import kotlin.test.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedEventRepositoryTest {
    private lateinit var repository: ExposedEventRepository

    @BeforeTest
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.drop(Comments, EventAttendees, Events, Users, Groups)
            SchemaUtils.create(Groups, Users, Events, EventAttendees, Comments)

            val now = OffsetDateTime.now()
            val groupId = Uuid.random()
            val userId = Uuid.random()

            Groups.insert {
                it[id] = groupId.toJavaUuid()
                it[name] = "Test Group"
                it[inviteCode] = "TESTGRP1"
                it[createdAt] = now
            }

            Users.insert {
                it[id] = userId.toJavaUuid()
                it[email] = "test@test.com"
                it[provider] = "KAKAO"
                it[providerId] = "test-provider-id"
                it[nickname] = "Test User"
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
        repository = ExposedEventRepository()
    }

    @AfterTest
    fun tearDown() {
        // H2 mem DB is cleared on close
    }

    @Test
    fun `getEventDetail - 이벤트를 상세 정보를 포함하여 조회한다`() {
        val eventId = Uuid.random()
        val userId = transaction {
            val uId = Users.selectAll().first()[Users.id]
            val gId = Groups.selectAll().first()[Groups.id]
            val now = OffsetDateTime.now()

            Events.insert {
                it[id] = eventId.toJavaUuid()
                it[groupId] = gId
                it[authorId] = uId
                it[title] = "Test Event"
                it[startAt] = now
                it[createdAt] = now
                it[updatedAt] = now
            }
            uId
        }

        transaction {
            val detail = repository.getEventDetail(eventId, Uuid.parse(userId.toString()))
            assertNotNull(detail)
            assertEquals("Test Event", detail.title)
            assertFalse(detail.isJoined)
        }
    }

    @Test
    fun `toggleJoin - 참여 상태를 토글한다`() {
        val (eventId, userId) = transaction {
            val uId = Users.selectAll().first()[Users.id]
            val gId = Groups.selectAll().first()[Groups.id]
            val eId = Uuid.random()
            val now = OffsetDateTime.now()

            Events.insert {
                it[id] = eId.toJavaUuid()
                it[groupId] = gId
                it[authorId] = uId
                it[title] = "Toggle Event"
                it[startAt] = now
                it[createdAt] = now
                it[updatedAt] = now
            }
            eId to Uuid.parse(uId.toString())
        }

        val firstToggle = transaction { repository.toggleJoin(eventId, userId) }
        assertTrue(firstToggle)

        val secondToggle = transaction { repository.toggleJoin(eventId, userId) }
        assertFalse(secondToggle)
    }
}
