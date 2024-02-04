package org.bmsk.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bmsk.lifemash.core.domain.usecase.NewsUseCase
import org.bmsk.model.NewsModel
import org.bmsk.model.section.SbsSection
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
) : ViewModel() {
    private val _newsStateFlow = MutableStateFlow<List<NewsModel>>(emptyList())
    val newsStateFlow = _newsStateFlow.asStateFlow()
    private val _newsImageLoadingFlow = MutableSharedFlow<Int>()
    val newsImageLoadingFlow = _newsImageLoadingFlow.asSharedFlow()
    private var processingJob: Job? = null

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        fetchNews(SbsSection.ECONOMICS)
    }

    fun fetchNews(section: SbsSection) {
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
                    _newsStateFlow.value = newsList
                    loadNewsImages(newsList)
                } catch (e: Exception) {
                    _errorFlow.emit(e)
                }
            }
        }
    }

    private suspend fun loadNewsImages(newsList: List<NewsModel>) {
        newsList.forEachIndexed { index, news ->
            withContext(Dispatchers.IO) {
                try {
                    val document = Jsoup.connect(news.link).get()
                    val imageUrl = document.select("meta[property=og:image]").attr("content")
                    news.imageUrl = imageUrl
                    _newsImageLoadingFlow.emit(index)
                } catch (e: Exception) {
                    _errorFlow.emit(e)
                }
            }
        }
    }
}
