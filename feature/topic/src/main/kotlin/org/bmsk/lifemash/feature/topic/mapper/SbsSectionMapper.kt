package org.bmsk.lifemash.feature.topic.mapper

import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.SectionChip

internal fun SBSSection.asChip(): SectionChip = when (this) {
    SBSSection.ECONOMICS -> SectionChip.ECONOMY
    SBSSection.SOCIAL -> SectionChip.SOCIAL
    SBSSection.ENTERTAINMENT_BROADCAST -> SectionChip.ENTERTAINMENT_BROADCAST
    SBSSection.LIFE_CULTURE -> SectionChip.LIFE_CULTURE
    SBSSection.POLITICS -> SectionChip.POLITICS
    SBSSection.INTERNATIONAL_GLOBAL -> SectionChip.INTERNATIONAL_GLOBAL
    SBSSection.SPORT -> SectionChip.SPORT
}
