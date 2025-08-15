package org.bmsk.lifemash.feature.topic

import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.LifeMashCategory
import org.bmsk.lifemash.core.model.section.SBSSection

internal data class TopicUiState(
    val query: String = "",
    val scrapingUiState: ScrapingUiState = ScrapingUiState.Idle,
    val selectedOverflowMenuNews: NewsModel? = null,
    val googleNewsModels: List<NewsModel> = emptyList(),
    val selectedCategory: LifeMashCategory = LifeMashCategory.BUSINESS,
    val categoryStates: List<CategoryUiState> = LifeMashCategory.entries.map {
        CategoryUiState(
            it,
            NewsLoadUiState.Loading
        )
    },
    val searchErrorEvent: Throwable? = null,
    val scrapErrorEvent: Throwable? = null,
) {

    fun getNewsLoadUiState(category: LifeMashCategory): NewsLoadUiState {
        return categoryStates.first { it.category == category }.newsLoadUiState
    }

    fun setNewsLoadUiState(category: LifeMashCategory, newsLoadUiState: NewsLoadUiState): TopicUiState {
        return copy(
            categoryStates = categoryStates.map {
                if (it.category == category) {
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

internal data class CategoryUiState(
    val category: LifeMashCategory,
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

internal sealed interface ScrapingUiState {
    data object Idle : ScrapingUiState
    data object IsScraping : ScrapingUiState
    data object ScrapCompleted : ScrapingUiState
    data class ScrapingError(val throwable: Throwable) : ScrapingUiState
}
