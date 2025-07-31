package org.bmsk.lifemash.feature.scrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.feature.scrap.usecase.DeleteScrapNewsUseCase
import org.bmsk.lifemash.feature.scrap.usecase.GetScrapNewsUseCase
import javax.inject.Inject

@HiltViewModel
internal class ScrapViewModel @Inject constructor(
    private val getScrapNewsUseCase: GetScrapNewsUseCase,
    private val deleteScrapNewsUseCase: DeleteScrapNewsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScrapUiState>(ScrapUiState.NewsLoading)
    val uiState = _uiState.asStateFlow()

    fun getScrapNews() {
        viewModelScope.launch {
            runCatching {
                getScrapNewsUseCase()
            }.onSuccess { newsModels ->
                if (newsModels.isEmpty()) {
                    _uiState.update { ScrapUiState.NewsEmpty }
                } else {
                    _uiState.update { ScrapUiState.NewsLoaded(newsModels.toPersistentList()) }
                }
            }.onFailure { t ->
                t.printStackTrace()
                _uiState.update { ScrapUiState.Error(t) }
            }
        }
    }

    fun deleteScrapNews(newsModel: NewsModel) {
        viewModelScope.launch {
            runCatching {
                deleteScrapNewsUseCase(newsModel)
                val newsModels = getScrapNewsUseCase()
                if (newsModels.isEmpty()) {
                    _uiState.update { ScrapUiState.NewsEmpty }
                } else {
                    _uiState.update { ScrapUiState.NewsLoaded(newsModels.toPersistentList()) }
                }
            }.onFailure { t ->
                _uiState.update { ScrapUiState.Error(t) }
            }
        }
    }
}