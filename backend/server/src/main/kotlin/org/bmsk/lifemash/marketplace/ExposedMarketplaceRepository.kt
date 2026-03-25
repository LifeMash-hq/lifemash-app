package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest
import org.bmsk.lifemash.db.tables.MarketplaceBlocks
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ExposedMarketplaceRepository : MarketplaceRepository {

    override fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto {
        return transaction {
            val newId = UUID.randomUUID()
            val now = System.currentTimeMillis()
            MarketplaceBlocks.insert {
                it[id] = newId
                it[name] = request.name
                it[url] = request.url
                it[description] = request.description
                it[iconUrl] = request.iconUrl
                it[MarketplaceBlocks.creatorId] = UUID.fromString(creatorId)
                it[status] = "PENDING"
                it[createdAt] = now
                it[toolsManifestUrl] = request.toolsManifestUrl
            }
            MarketplaceBlockDto(
                id = newId.toString(),
                name = request.name,
                url = request.url,
                description = request.description,
                iconUrl = request.iconUrl,
                status = "PENDING",
                createdAt = now,
            )
        }
    }

    override fun getApproved(): List<MarketplaceBlockDto> = transaction {
        MarketplaceBlocks
            .selectAll().where { MarketplaceBlocks.status eq "APPROVED" }
            .map { it.toDto() }
    }

    override fun getMine(creatorId: String): List<MarketplaceBlockDto> = transaction {
        MarketplaceBlocks
            .selectAll().where { MarketplaceBlocks.creatorId eq UUID.fromString(creatorId) }
            .map { it.toDto() }
    }

    override fun updateStatus(id: String, status: String): MarketplaceBlockDto? = transaction {
        val uuid = UUID.fromString(id)
        MarketplaceBlocks.update({ MarketplaceBlocks.id eq uuid }) {
            it[MarketplaceBlocks.status] = status
        }
        MarketplaceBlocks
            .selectAll().where { MarketplaceBlocks.id eq uuid }
            .map { it.toDto() }
            .firstOrNull()
    }

    override fun findById(id: String): MarketplaceBlockDto? {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return null
        return transaction {
            MarketplaceBlocks
                .selectAll().where { MarketplaceBlocks.id eq uuid }
                .map { it.toDto() }
                .firstOrNull()
        }
    }

    override fun updateStatusAndTools(
        id: String,
        status: String,
        toolDefinitions: String?,
    ): MarketplaceBlockDto? = transaction {
        val uuid = UUID.fromString(id)
        MarketplaceBlocks.update({ MarketplaceBlocks.id eq uuid }) {
            it[MarketplaceBlocks.status] = status
            it[MarketplaceBlocks.toolDefinitions] = toolDefinitions
        }
        MarketplaceBlocks
            .selectAll().where { MarketplaceBlocks.id eq uuid }
            .map { it.toDto() }
            .firstOrNull()
    }

    private fun ResultRow.toDto() = MarketplaceBlockDto(
        id = this[MarketplaceBlocks.id].toString(),
        name = this[MarketplaceBlocks.name],
        url = this[MarketplaceBlocks.url],
        description = this[MarketplaceBlocks.description],
        iconUrl = this[MarketplaceBlocks.iconUrl],
        status = this[MarketplaceBlocks.status],
        createdAt = this[MarketplaceBlocks.createdAt],
        toolsManifestUrl = this[MarketplaceBlocks.toolsManifestUrl],
        toolDefinitions = this[MarketplaceBlocks.toolDefinitions],
    )
}
