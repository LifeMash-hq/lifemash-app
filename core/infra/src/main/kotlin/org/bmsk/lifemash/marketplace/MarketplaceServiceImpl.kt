package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

class MarketplaceServiceImpl(
    private val repository: MarketplaceRepository,
    private val toolManifestFetcher: ToolManifestFetcher,
) : MarketplaceService {

    override fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto =
        repository.submit(creatorId, request)

    override fun getApproved(): List<MarketplaceBlockDto> =
        repository.getApproved()

    override fun getMine(creatorId: String): List<MarketplaceBlockDto> =
        repository.getMine(creatorId)

    override suspend fun approve(id: String): MarketplaceBlockDto? {
        val block = repository.findById(id) ?: return null

        val toolDefinitions = block.toolsManifestUrl?.let {
            toolManifestFetcher.fetch(it)
        }

        return repository.updateStatusAndTools(id, "APPROVED", toolDefinitions)
    }

    override fun reject(id: String): MarketplaceBlockDto? =
        repository.updateStatus(id, "REJECTED")
}
