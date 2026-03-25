package org.bmsk.lifemash.home.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.bmsk.lifemash.home.api.MarketplaceBlockInfo
import org.bmsk.lifemash.home.domain.repository.MarketplaceRepository
import org.bmsk.lifemash.model.marketplace.MarketplaceBlockDto

class MarketplaceRepositoryImpl(
    private val client: HttpClient,
) : MarketplaceRepository {

    override suspend fun getApprovedBlocks(): List<MarketplaceBlockInfo> {
        val dtos = client.get("/api/v1/marketplace/blocks").body<List<MarketplaceBlockDto>>()
        return dtos.map {
            MarketplaceBlockInfo(
                id = it.id,
                name = it.name,
                url = it.url,
                description = it.description,
                iconUrl = it.iconUrl,
            )
        }
    }
}
