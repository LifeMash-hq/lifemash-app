package org.bmsk.lifemash.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun fetchNews(section: SbsSection) = viewModelScope.launch {
        _uiState.update { it.copy(newsList = persistentListOf(), currentSection = section) }
        processNewsFetching(section)
    }

    fun fetchNewsSearchResults(query: String) = viewModelScope.launch {
        _uiState.update { it.copy(newsList = persistentListOf()) }
        processNewsFetchingForSearch(query)
    }

    private fun processNewsFetchingForSearch(query: String) = viewModelScope.launch {
        runCatching {
            newsUseCase.getGoogleNews(query)
        }.fold(
            onSuccess = { news ->
                val newsWithImages = fetchImageUrls(news)
                _uiState.update { it.copy(newsList = newsWithImages.toPersistentList()) }
            },
            onFailure = { error -> _errorFlow.emit(error) },
        )
    }

    private fun processNewsFetching(section: SbsSection) = viewModelScope.launch {
        runCatching {
            newsUseCase.getSbsNews(section)
        }.fold(
            onSuccess = { news ->
                val newsWithImages = fetchImageUrls(news)
                _uiState.update { it.copy(newsList = newsWithImages.toPersistentList()) }
            },
            onFailure = { error -> _errorFlow.emit(error) },
        )
    }

    private suspend fun fetchImageUrls(newsList: List<NewsModel>): List<NewsModel> =
        coroutineScope {
            newsList.map { news ->
                async(Dispatchers.IO) {
                    Jsoup.connect(news.link).get().run {
                        val imageUrl = select("meta[property=og:image]").attr("content")
                        news.copy(imageUrl = imageUrl)
                    }
                }
            }.awaitAll()
        }
}
