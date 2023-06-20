package org.bmsk.topic.ui.topic

import org.bmsk.model.section.SbsSection
import org.bmsk.topic.R

enum class ChipSection(val chipId: Int, val section: SbsSection) {
    ECONOMY(R.id.economyChip, SbsSection.ECONOMICS),
    POLITICS(R.id.politicsChip, SbsSection.POLITICS),
    SOCIAL(R.id.socialChip, SbsSection.SOCIAL),
    LIFE_CULTURE(R.id.lifeCultureChip, SbsSection.LIFE_CULTURE),
    INTERNATIONAL_GLOBAL(R.id.internationalGlobalChip, SbsSection.INTERNATIONAL_GLOBAL),
    ENTERTAINMENT_BROADCAST(R.id.entertainmentBroadcastChip, SbsSection.ENTERTAINMENT_BROADCAST),
    SPORT(R.id.sportChip, SbsSection.SPORT);

    companion object {
        fun getSectionByChipId(chipId: Int): SbsSection {
            return values().first { it.chipId == chipId }.section
        }
    }
}