package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

class StubMarketplaceService : MarketplaceService {
    private fun demoBlock(id: String = "demo-block-1", name: String = "Demo Block") = MarketplaceBlockDto(
        id = id,
        name = name,
        url = "https://demo.lifemash.app",
        description = "Demo block",
        iconUrl = null,
        status = "APPROVED",
        createdAt = 0L,
    )

    override fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto =
        demoBlock(name = request.name)

    override fun getApproved(): List<MarketplaceBlockDto> =
        listOf(demoBlock())

    override fun getMine(creatorId: String): List<MarketplaceBlockDto> =
        emptyList()

    override suspend fun approve(id: String): MarketplaceBlockDto? =
        demoBlock(id = id)

    override fun reject(id: String): MarketplaceBlockDto? =
        demoBlock(id = id).copy(status = "REJECTED")
}
