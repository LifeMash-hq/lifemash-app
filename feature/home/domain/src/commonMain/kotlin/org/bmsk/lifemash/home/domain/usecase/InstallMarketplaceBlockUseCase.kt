package org.bmsk.lifemash.home.domain.usecase

import kotlinx.coroutines.flow.first
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.api.MarketplaceBlockInfo
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository

class InstallMarketplaceBlockUseCase(private val repository: HomeLayoutRepository) {
    suspend operator fun invoke(block: MarketplaceBlockInfo) {
        val current = repository.getLayout().first()
        val alreadyInstalled = current.any { it is HomeBlock.WebViewBlock && it.blockId == block.id }
        if (alreadyInstalled) return
        val updated = current + HomeBlock.WebViewBlock(blockId = block.id, url = block.url, visible = true)
        repository.saveLayout(updated)
    }
}
