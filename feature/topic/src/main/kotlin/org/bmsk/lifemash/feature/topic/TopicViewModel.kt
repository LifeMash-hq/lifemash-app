package org.bmsk.lifemash.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bmsk.lifemash.core.domain.usecase.NewsUseCase
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SbsSection
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
) : ViewModel() {
    private var processingJob: Job? = null
    private var updatedNewsList: MutableList<NewsModel> = mutableListOf()

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
            processingJob?.cancel()
            processingJob = launch {
                runCatching {
                    updatedNewsList = fetcher().sortedBy { it.link }.toMutableList()
                    _uiState.update { it.copy(newsList = updatedNewsList.toPersistentList()) }

                    fetchImageUrls(updatedNewsList).collect { updatedNews ->
                        val index = updatedNewsList.binarySearchBy(updatedNews.link) { it.link }
                        if (index < 0) return@collect // 찾지 못한 경우

                        updatedNewsList[index] = updatedNews
                    }

                    _uiState.update { state -> state.copy(newsList = updatedNewsList.toPersistentList()) }
                }.onFailure { _errorFlow.emit(it) }
            }
        }
    }

    private suspend fun fetchImageUrls(newsList: List<NewsModel>): Flow<NewsModel> = channelFlow {
        newsList.forEach { news ->
            launch(Dispatchers.IO) {
                val document = Jsoup.connect(news.link).get()
                val imageUrl = document.select("meta[property=og:image]").attr("content")
                val updatedNews = news.copy(imageUrl = imageUrl)
                send(updatedNews)
            }
        }
    }
}
