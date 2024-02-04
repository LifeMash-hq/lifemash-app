package org.bmsk.feature.topic

import org.bmsk.core.model.section.SbsSection
import org.bmsk.lifemash.feature.topic.R

internal enum class SectionChip(val section: SbsSection, val chipNameId: Int) {
    ECONOMY(SbsSection.ECONOMICS, R.string.economy),

    POLITICS(SbsSection.POLITICS, R.string.politics), SOCIAL(
        SbsSection.SOCIAL,
        R.string.social,
    ),
    LIFE_CULTURE(
        SbsSection.LIFE_CULTURE,
        R.string.life_culture,
    ),
    INTERNATIONAL_GLOBAL(

        SbsSection.INTERNATIONAL_GLOBAL,
        R.string.international_global,
    ),
    ENTERTAINMENT_BROADCAST(

        SbsSection.ENTERTAINMENT_BROADCAST,
        R.string.entertainment_broadcast,
    ),
    SPORT(SbsSection.SPORT, R.string.sport),
}
