package org.bmsk.lifemash.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.home.api.HomeBlock
import org.bmsk.lifemash.home.domain.repository.HomeLayoutRepository

class GetHomeLayoutUseCase(private val repository: HomeLayoutRepository) {
    operator fun invoke(): Flow<List<HomeBlock>> = repository.getLayout()
}
