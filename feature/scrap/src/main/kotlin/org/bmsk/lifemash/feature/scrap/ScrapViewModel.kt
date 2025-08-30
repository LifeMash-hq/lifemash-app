package org.bmsk.lifemash.feature.scrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.domain.core.model.ArticleId
import org.bmsk.lifemash.domain.scrap.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.domain.scrap.usecase.GetScrappedArticlesUseCase
import javax.inject.Inject

@HiltViewModel
internal class ScrapViewModel @Inject constructor(
    private val getScrappedArticlesUseCase: GetScrappedArticlesUseCase,
    private val deleteScrappedArticleUseCase: DeleteScrappedArticleUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScrapUiState>(ScrapUiState.NewsLoading)
    val uiState = _uiState.asStateFlow()

    fun getScrapNews() {
        viewModelScope.launch {
            runCatching {
                getScrappedArticlesUseCase()
            }.onSuccess { articles ->
                if (articles.isEmpty()) {
                    _uiState.update { ScrapUiState.NewsEmpty }
                } else {
                    val scrapUiModels = articles.map { ScrapUiModel.from(it) }.toPersistentList()
                    _uiState.update { ScrapUiState.NewsLoaded(scrapUiModels) }
                }
            }.onFailure { t ->
                t.printStackTrace()
                _uiState.update { ScrapUiState.Error(t) }
            }
        }
    }

    fun deleteScrapNews(scrap: ScrapUiModel) {
        viewModelScope.launch {
            runCatching {
                deleteScrappedArticleUseCase(ArticleId(scrap.id))
                // Re-fetch the list after deletion
                val articles = getScrappedArticlesUseCase()
                if (articles.isEmpty()) {
                    _uiState.update { ScrapUiState.NewsEmpty }
                } else {
                    val scrapUiModels = articles.map { ScrapUiModel.from(it) }.toPersistentList()
                    _uiState.update { ScrapUiState.NewsLoaded(scrapUiModels) }
                }
            }.onFailure { t ->
                _uiState.update { ScrapUiState.Error(t) }
            }
        }
    }
}
