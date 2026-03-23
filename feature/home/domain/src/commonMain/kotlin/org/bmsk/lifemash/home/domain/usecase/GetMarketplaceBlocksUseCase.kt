package org.bmsk.lifemash.home.domain.usecase

import org.bmsk.lifemash.home.api.MarketplaceBlockInfo
import org.bmsk.lifemash.home.domain.repository.MarketplaceRepository

class GetMarketplaceBlocksUseCase(private val repository: MarketplaceRepository) {
    suspend operator fun invoke(): List<MarketplaceBlockInfo> = repository.getApprovedBlocks()
}
