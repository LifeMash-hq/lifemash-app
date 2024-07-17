package org.bmsk.lifemash.feature.topic

import kotlinx.collections.immutable.PersistentList
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection

internal data class TopicUiState(
    val currentSection: SBSSection,
    val newsList: PersistentList<NewsModel>,
) {
    val isLoading: Boolean
        get() = newsList.isEmpty()
}
