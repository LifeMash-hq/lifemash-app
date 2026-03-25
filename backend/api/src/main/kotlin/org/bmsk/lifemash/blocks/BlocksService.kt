package org.bmsk.lifemash.blocks

import org.bmsk.lifemash.model.blocks.BlocksTodayResponse

interface BlocksService {
    fun getTodayData(userId: String): BlocksTodayResponse
}
