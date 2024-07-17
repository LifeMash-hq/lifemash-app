package org.bmsk.lifemash.feature.topic

import org.bmsk.lifemash.core.model.section.SBSSection

internal enum class SectionChip(val section: SBSSection, val chipNameId: Int) {
    ECONOMY(SBSSection.ECONOMICS, R.string.economy),

    POLITICS(SBSSection.POLITICS, R.string.politics), SOCIAL(
        SBSSection.SOCIAL,
        R.string.social,
    ),
    LIFE_CULTURE(
        SBSSection.LIFE_CULTURE,
        R.string.life_culture,
    ),
    INTERNATIONAL_GLOBAL(

        SBSSection.INTERNATIONAL_GLOBAL,
        R.string.international_global,
    ),
    ENTERTAINMENT_BROADCAST(

        SBSSection.ENTERTAINMENT_BROADCAST,
        R.string.entertainment_broadcast,
    ),
    SPORT(SBSSection.SPORT, R.string.sport),
}
