package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.marketplace.MarketplaceRepository
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

class FakeMarketplaceRepository : MarketplaceRepository {
    private val blocks = mutableListOf<MarketplaceBlockDto>()

    override fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto {
        val block = MarketplaceBlockDto(
            id = "fake-id-${blocks.size}",
            name = request.name,
            url = request.url,
            description = request.description,
            iconUrl = request.iconUrl,
            status = "pending",
            createdAt = System.currentTimeMillis(),
            toolsManifestUrl = request.toolsManifestUrl,
            toolDefinitions = null,
        )
        blocks.add(block)
        return block
    }

    override fun getApproved(): List<MarketplaceBlockDto> =
        blocks.filter { it.status == "approved" }

    override fun getMine(creatorId: String): List<MarketplaceBlockDto> = blocks.toList()

    override fun updateStatus(id: String, status: String): MarketplaceBlockDto? {
        val idx = blocks.indexOfFirst { it.id == id }
        if (idx == -1) return null
        val updated = blocks[idx].copy(status = status)
        blocks[idx] = updated
        return updated
    }

    override fun findById(id: String): MarketplaceBlockDto? =
        blocks.firstOrNull { it.id == id }

    override fun updateStatusAndTools(id: String, status: String, toolDefinitions: String?): MarketplaceBlockDto? {
        val idx = blocks.indexOfFirst { it.id == id }
        if (idx == -1) return null
        val updated = blocks[idx].copy(status = status, toolDefinitions = toolDefinitions)
        blocks[idx] = updated
        return updated
    }
}
