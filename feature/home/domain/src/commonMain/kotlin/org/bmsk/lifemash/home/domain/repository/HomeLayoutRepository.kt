package org.bmsk.lifemash.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.home.api.HomeBlock

interface HomeLayoutRepository {
    fun getLayout(): Flow<List<HomeBlock>>
    suspend fun saveLayout(blocks: List<HomeBlock>)
}
