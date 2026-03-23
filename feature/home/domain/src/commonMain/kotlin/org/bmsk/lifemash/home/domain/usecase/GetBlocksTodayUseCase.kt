package org.bmsk.lifemash.home.domain.usecase

import org.bmsk.lifemash.home.api.BlocksTodayData
import org.bmsk.lifemash.home.domain.repository.BlocksTodayRepository

class GetBlocksTodayUseCase(private val repository: BlocksTodayRepository) {
    suspend operator fun invoke(): BlocksTodayData = repository.getTodayData()
}
