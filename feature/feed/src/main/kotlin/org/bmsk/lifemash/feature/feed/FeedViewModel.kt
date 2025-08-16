package org.bmsk.lifemash.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.repo.article.api.ArticleCategory
import org.bmsk.lifemash.feature.feed.usecase.GetArticlesUseCase
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun getArticles(category: ArticleCategory) {
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.setLoading(category) }
                val articles = getArticlesUseCase(category)
                    .map { ArticleUi.from(it) }
                    .toPersistentList()

                _uiState.update { it.setLoaded(category, articles) }
            }.onFailure { t ->
                TODO()
            }
        }
    }

    fun setQueryText(queryText: String) {
        _uiState.update { it.setQuery(queryText) }
    }

    fun setSearchMode(isSearchMode: Boolean) {
        _uiState.update { it.setSearchMode(isSearchMode) }
    }

    fun setCategory(category: ArticleCategory) {
        _uiState.update { it.setCategory(category) }
    }
}