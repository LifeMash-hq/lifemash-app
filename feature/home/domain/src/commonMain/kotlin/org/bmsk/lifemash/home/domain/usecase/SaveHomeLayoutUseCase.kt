package org.bmsk.lifemash.home.domain.usecase

import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository

class SaveHomeLayoutUseCase(private val repository: HomeLayoutRepository) {
    suspend operator fun invoke(blocks: List<HomeBlock>) = repository.saveLayout(blocks)
}
