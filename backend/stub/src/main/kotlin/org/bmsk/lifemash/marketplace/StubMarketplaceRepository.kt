package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

class StubMarketplaceRepository : MarketplaceRepository {
    private val blocks = mutableMapOf<String, MarketplaceBlockDto>()

    override fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto {
        val id = "stub-${blocks.size + 1}"
        val block = MarketplaceBlockDto(
            id = id,
            name = request.name,
            url = request.url,
            description = request.description,
            iconUrl = request.iconUrl,
            status = "PENDING",
            createdAt = System.currentTimeMillis(),
            toolsManifestUrl = request.toolsManifestUrl,
        )
        blocks[id] = block
        return block
    }

    override fun getApproved(): List<MarketplaceBlockDto> =
        blocks.values.filter { it.status == "APPROVED" }

    override fun getMine(creatorId: String): List<MarketplaceBlockDto> =
        blocks.values.toList()

    override fun updateStatus(id: String, status: String): MarketplaceBlockDto? {
        val block = blocks[id] ?: return null
        val updated = block.copy(status = status)
        blocks[id] = updated
        return updated
    }

    override fun findById(id: String): MarketplaceBlockDto? =
        blocks[id]

    override fun updateStatusAndTools(id: String, status: String, toolDefinitions: String?): MarketplaceBlockDto? {
        val block = blocks[id] ?: return null
        val updated = block.copy(status = status, toolDefinitions = toolDefinitions)
        blocks[id] = updated
        return updated
    }
}
