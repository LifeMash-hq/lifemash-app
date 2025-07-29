package org.bmsk.lifemash.feature.topic

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection

internal data class TopicUiState(
    val query: String = "",
    val scrapingUiState: ScrapingUiState = ScrapingUiState.Idle,
    val selectedOverflowMenuNews: NewsModel? = null,
    val googleNewsModels: List<NewsModel> = emptyList(),
    val selectedSection: SBSSection = SBSSection.ECONOMICS,
    val sectionStates: List<SectionUiState> = SBSSection.entries.map {
        SectionUiState(
            it,
            NewsLoadUiState.Loading
        )
    },
    val searchErrorEvent: Throwable? = null,
    val scrapErrorEvent: Throwable? = null,
) {
    fun getNewsLoadUiState(section: SBSSection): NewsLoadUiState {
        return sectionStates.first { it.section == section }.newsLoadUiState
    }

    fun setNewsLoadUiState(section: SBSSection, newsLoadUiState: NewsLoadUiState): TopicUiState {
        return copy(
            sectionStates = sectionStates.map {
                if (it.section == section) {
                    it.copy(newsLoadUiState = newsLoadUiState)
                } else {
                    it
                }
            }
        )
    }
}

internal data class SectionUiState(
    val section: SBSSection,
    val newsLoadUiState: NewsLoadUiState
)

internal sealed interface NewsLoadUiState {

    data object Loading : NewsLoadUiState

    data class Loaded(
        val newsModels: List<NewsModel>
    ) : NewsLoadUiState

    data class Error(
        val throwable: Throwable
    ) : NewsLoadUiState
}

internal interface ScrapingUiState {
    data object Idle : ScrapingUiState
    data object IsScraping : ScrapingUiState
    data object ScrapCompleted : ScrapingUiState
    data class ScrapingError(val throwable: Throwable) : ScrapingUiState
}
