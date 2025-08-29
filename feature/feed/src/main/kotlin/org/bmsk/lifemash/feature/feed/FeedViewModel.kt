package org.bmsk.lifemash.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.core.model.ArticleCategory
import org.bmsk.lifemash.domain.feed.usecase.GetArticlesUseCase
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun getArticles(category: ArticleCategory) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadStateByCategory = it.loadStateByCategory.put(
                        category,
                        LoadState.Loading
                    )
                )
            }

            runCatching {
                getArticlesUseCase(category)
            }.onSuccess { articles ->
                val articleUis = articles
                    .map { ArticleUi.from(it) }
                    .toPersistentList()

                _uiState.update { currentState ->
                    val mergedArticlesById = currentState
                        .articlesById
                        .putAll(
                            m = articleUis
                                .associateBy(ArticleUi::id)
                                .toPersistentMap()
                        )
                    val newIds = articleUis.map { it.id }.toPersistentList()

                    val newState = currentState.copy(
                        articlesById = mergedArticlesById,
                        idsByCategory = currentState.idsByCategory.put(category, newIds),
                        loadStateByCategory = currentState.loadStateByCategory.put(
                            category,
                            LoadState.Loaded
                        )
                    )
                    newState.copy(visibleArticles = recalculateVisibleArticles(newState))
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        loadStateByCategory = it.loadStateByCategory.put(
                            category,
                            LoadState.Error(throwable)
                        )
                    )
                }
            }
        }
    }

    fun setQueryText(queryText: String) {
        _uiState.update { it.copy(queryText = queryText) }
    }

    fun setSearchMode(isSearchMode: Boolean) {
        _uiState.update { it.copy(isSearchMode = isSearchMode) }
    }

    fun setCategory(category: ArticleCategory) {
        _uiState.update { currentState ->
            val newState = currentState.copy(selectedCategory = category)
            newState.copy(visibleArticles = recalculateVisibleArticles(newState))
        }
    }

    private fun recalculateVisibleArticles(state: FeedUiState): PersistentList<ArticleUi> {
        val articles = if (state.selectedCategory == ArticleCategory.ALL) {
            ArticleCategory.entries.asSequence()
                .flatMap { (state.idsByCategory[it] ?: persistentListOf()).asSequence() }
                .distinct()
                .mapNotNull(state.articlesById::get)
        } else {
            (state.idsByCategory[state.selectedCategory] ?: persistentListOf()).asSequence()
                .mapNotNull(state.articlesById::get)
        }
        return articles.sortedByDescending { it.publishedAtInstant }.toPersistentList()
    }
}