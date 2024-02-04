package org.bmsk.feature.topic

import org.bmsk.core.model.NewsModel
import org.bmsk.core.model.section.SbsSection

internal data class TopicUiState(
    val currentSection: SbsSection = SbsSection.ECONOMICS,
    val newsList: List<NewsModel> = emptyList(),
)
