package org.bmsk.lifemash.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.model.NewsModel
import org.bmsk.lifemash.core.model.section.SBSSection
import org.bmsk.lifemash.feature.topic.usecase.GetGoogleNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.GetSBSNewsUseCase
import org.bmsk.lifemash.feature.topic.usecase.ScrapNewsUseCase
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val getSBSNewsUseCase: GetSBSNewsUseCase,
    private val getGoogleNewsUseCase: GetGoogleNewsUseCase,
    private val scrapNewsUseCase: ScrapNewsUseCase,
) : ViewModel() {
    private var currentIoJob: Job? = null

    private val _uiState = MutableStateFlow(
        TopicUiState(
            currentSection = SBSSection.ECONOMICS,
            newsList = persistentListOf(),
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        fetchNews(SBSSection.ECONOMICS)
    }

    fun scrapNews(newsModel: NewsModel) {
        viewModelScope.launch {
            scrapNewsUseCase(newsModel)
        }
    }

    fun fetchNews(section: SBSSection) = viewModelScope.launch {
        currentIoJob?.cancel()
        _uiState.update { it.copy(newsList = persistentListOf(), currentSection = section) }
        currentIoJob = processNewsFetching(section)
    }

    fun fetchNewsSearchResults(query: String) = viewModelScope.launch {
        currentIoJob?.cancel()
        _uiState.update { it.copy(newsList = persistentListOf()) }
        currentIoJob = processNewsFetchingForSearch(query)
    }

    private fun processNewsFetchingForSearch(query: String) = viewModelScope.launch {
        runCatching {
            getGoogleNewsUseCase(query)
        }.fold(
            onSuccess = { newsItems ->
                handleNewsSuccess(newsItems)
            },
            onFailure = { error -> _errorFlow.emit(error) },
        )
    }

    private fun processNewsFetching(section: SBSSection) = viewModelScope.launch {
        runCatching {
            getSBSNewsUseCase(section)
        }.fold(
            onSuccess = { news ->
                handleNewsSuccess(news)
            },
            onFailure = { error ->
                handleNewsFailure(error)
            },
        )
    }

    private suspend fun handleNewsSuccess(newsItems: List<NewsModel>) {
        _uiState.update { it.copy(newsList = newsItems.toPersistentList()) }
        newsItems.forEachIndexed { index, newsItem ->
            fetchAndSetImageUrl(index, newsItem)
        }
    }

    private suspend fun handleNewsFailure(error: Throwable) {
        _errorFlow.emit(error)
    }

    private suspend fun fetchAndSetImageUrl(index: Int, news: NewsModel) {
        val imageUrl = fetchImageUrl(news.link)
        updateNewsItemWithImageUrl(index, news, imageUrl)
    }

    private suspend fun fetchImageUrl(newsLink: String): String = withContext(Dispatchers.IO) {
        Jsoup.connect(newsLink).get().select("meta[property=og:image]").attr("content")
    }

    private fun updateNewsItemWithImageUrl(
        index: Int,
        newsItem: NewsModel,
        imageUrl: String,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                newsList = currentState.newsList.set(index, newsItem.copy(imageUrl = imageUrl)),
            )
        }
    }
}
