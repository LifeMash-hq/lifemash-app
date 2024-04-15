package org.bmsk.lifemash.feature.topic

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection

internal data class TopicUiState(
    val currentSection: SbsSection,
    val newsList: PersistentList<NewsModel>,
) {
    val isLoading: Boolean
        get() = newsList.isEmpty()
}
