package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

interface MarketplaceService {
    fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto
    fun getApproved(): List<MarketplaceBlockDto>
    fun getMine(creatorId: String): List<MarketplaceBlockDto>
    suspend fun approve(id: String): MarketplaceBlockDto?
    fun reject(id: String): MarketplaceBlockDto?
}
