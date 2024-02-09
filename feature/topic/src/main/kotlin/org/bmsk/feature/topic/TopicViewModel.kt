package org.bmsk.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.core.model.NewsModel
import org.bmsk.core.model.section.SbsSection
import org.bmsk.lifemash.core.domain.usecase.NewsUseCase
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
) : ViewModel() {
    private var processingJob: Job? = null

    private val _uiState = MutableStateFlow(TopicUiState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        fetchNews(SbsSection.ECONOMICS)
    }

    fun fetchNews(section: SbsSection) {
        _uiState.update { it.copy(currentSection = section) }
        processNewsFetching { newsUseCase.getSbsNews(section).first() }
    }

    fun fetchNewsSearchResults(query: String) {
        processNewsFetching { newsUseCase.getGoogleNews(query).first() }
    }

    fun bookmark(newsItem: NewsModel) {
    }

    private fun processNewsFetching(fetcher: suspend () -> List<NewsModel>) {
        viewModelScope.launch {
            processingJob?.cancel() // 코루틴 작업 취소
            processingJob = launch {
                try {
                    val newsList = fetcher()
                    _uiState.update { it.copy(newsList = newsList) }
                } catch (e: Exception) {
                    _errorFlow.emit(e)
                }
            }
        }
    }
}
