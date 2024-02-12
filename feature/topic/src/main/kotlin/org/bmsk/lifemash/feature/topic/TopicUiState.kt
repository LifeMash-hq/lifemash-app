package org.bmsk.lifemash.feature.topic

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection

internal data class TopicUiState(
    val currentSection: SbsSection = SbsSection.ECONOMICS,
    val newsList: List<NewsModel> = emptyList(),
)
