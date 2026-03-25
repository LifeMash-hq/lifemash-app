package org.bmsk.lifemash.blocks

import org.bmsk.lifemash.model.blocks.BlocksTodayResponse

class StubBlocksService : BlocksService {
    override fun getTodayData(userId: String): BlocksTodayResponse =
        BlocksTodayResponse(
            todayEvents = emptyList(),
            groups = emptyList(),
        )
}
