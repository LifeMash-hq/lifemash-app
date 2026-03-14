package org.bmsk.lifemash.scrap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bmsk.lifemash.scrap.domain.usecase.DeleteScrappedArticleUseCase
import org.bmsk.lifemash.scrap.domain.usecase.GetScrappedArticlesUseCase
import javax.inject.Inject

@HiltViewModel
internal class ScrapViewModel @Inject constructor(
    getScrappedArticlesUseCase: GetScrappedArticlesUseCase,
    private val deleteScrappedArticleUseCase: DeleteScrappedArticleUseCase
) : ViewModel() {

    val uiState: StateFlow<ScrapUiState> = getScrappedArticlesUseCase()
        .map {
            if (it.isEmpty()) {
                ScrapUiState.NewsEmpty
            } else {
                val scrapUiModels = it.map { article -> ScrapArticleUi.from(article) }.toPersistentList()
                ScrapUiState.NewsLoaded(scrapUiModels)
            }
        }
        .onStart { emit(ScrapUiState.NewsLoading) }
        .catch { emit(ScrapUiState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScrapUiState.NewsLoading
        )

    fun deleteScrapNews(scrap: ScrapArticleUi) {
        viewModelScope.launch {
            deleteScrappedArticleUseCase(scrap.article.id)
        }
    }
}