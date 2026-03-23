package org.bmsk.lifemash.home.domain.repository

import org.bmsk.lifemash.home.api.MarketplaceBlockInfo

interface MarketplaceRepository {
    suspend fun getApprovedBlocks(): List<MarketplaceBlockInfo>
}
