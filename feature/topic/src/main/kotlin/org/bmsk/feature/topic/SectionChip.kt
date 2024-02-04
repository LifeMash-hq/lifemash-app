package org.bmsk.feature.topic

import org.bmsk.core.model.section.SbsSection
import org.bmsk.lifemash.feature.topic.R

internal enum class SectionChip(val chipId: Int, val section: SbsSection, val chipNameId: Int) {
    ECONOMY(R.id.economyChip, SbsSection.ECONOMICS, R.string.economy), POLITICS(
        R.id.politicsChip,
        SbsSection.POLITICS,
        R.string.politics,
    ),
    SOCIAL(R.id.socialChip, SbsSection.SOCIAL, R.string.social), LIFE_CULTURE(
        R.id.lifeCultureChip,
        SbsSection.LIFE_CULTURE,
        R.string.life_culture,
    ),
    INTERNATIONAL_GLOBAL(
        R.id.internationalGlobalChip,
        SbsSection.INTERNATIONAL_GLOBAL,
        R.string.international_global,
    ),
    ENTERTAINMENT_BROADCAST(
        R.id.entertainmentBroadcastChip,
        SbsSection.ENTERTAINMENT_BROADCAST,
        R.string.entertainment_broadcast,
    ),
    SPORT(R.id.sportChip, SbsSection.SPORT, R.string.sport), ;

    companion object {
        fun getSectionByChipId(chipId: Int): SbsSection {
            return entries.first { it.chipId == chipId }.section
        }
    }
}
