package org.bmsk.lifemash.home.domain.repository

import org.bmsk.lifemash.home.api.BlocksTodayData

interface BlocksTodayRepository {
    suspend fun getTodayData(): BlocksTodayData
}
