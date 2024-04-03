package org.bmsk.lifemash.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.domain.usecase.NewsUseCase
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
) : ViewModel() {
    private var processingJob: Job? = null

    private val _uiState = MutableStateFlow(
        TopicUiState(
            currentSection = SbsSection.ECONOMICS,
            newsList = persistentListOf(),
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        fetchNews(SbsSection.ECONOMICS)
    }

    fun fetchNews(section: SbsSection) {
        _uiState.update { it.copy(currentSection = section) }
        processNewsFetching { newsUseCase.getSbsNews(section) }
    }

    fun fetchNewsSearchResults(query: String) {
        processNewsFetching { newsUseCase.getGoogleNews(query) }
    }

    private fun processNewsFetching(fetcher: suspend () -> List<NewsModel>) {
        viewModelScope.launch {
            processingJob?.cancel() // 코루틴 작업 취소
            processingJob = launch {
                try {
                    val newsList = fetcher()
                    _uiState.update { it.copy(newsList = newsList.toPersistentList()) }
                } catch (e: Exception) {
                    _errorFlow.emit(e)
                }
            }
        }
    }
}
