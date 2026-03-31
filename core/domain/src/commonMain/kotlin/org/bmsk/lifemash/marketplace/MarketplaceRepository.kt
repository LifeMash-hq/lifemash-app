package org.bmsk.lifemash.marketplace

import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest

interface MarketplaceRepository {
    fun submit(creatorId: String, request: SubmitBlockRequest): MarketplaceBlockDto
    fun getApproved(): List<MarketplaceBlockDto>
    fun getMine(creatorId: String): List<MarketplaceBlockDto>
    fun updateStatus(id: String, status: String): MarketplaceBlockDto?
    fun findById(id: String): MarketplaceBlockDto?
    fun updateStatusAndTools(id: String, status: String, toolDefinitions: String?): MarketplaceBlockDto?
}
