package org.bmsk.lifemash.explore

import org.bmsk.lifemash.db.tables.Events
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.lowerCase

class ExposedExploreRepository : ExploreRepository {

    override fun searchUsers(query: String): List<UserSummaryDto> = transaction {
        val searchPattern = "%${query.lowercase()}%"
        Users.selectAll()
            .where { (Users.nickname.lowerCase() like searchPattern) or (Users.email.lowerCase() like searchPattern) }
            .limit(50)
            .map {
                UserSummaryDto(
                    id = it[Users.id].toString(),
                    nickname = it[Users.nickname],
                    profileImage = it[Users.profileImage]
                )
            }
    }

    override fun searchEvents(query: String): List<EventSummaryDto> = transaction {
        val searchPattern = "%${query.lowercase()}%"
        Events.selectAll()
            .where { (Events.title.lowerCase() like searchPattern) }
            .orderBy(Events.createdAt to SortOrder.DESC)
            .limit(50)
            .map {
                EventSummaryDto(
                    id = it[Events.id].toString(),
                    title = it[Events.title],
                    startAt = it[Events.startAt].toString(),
                    color = it[Events.color]
                )
            }
    }
}
