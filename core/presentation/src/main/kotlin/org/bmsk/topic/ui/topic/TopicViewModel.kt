package org.bmsk.topic.ui.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bmsk.domain.usecase.NewsUseCase
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

    init {
        fetchNews(SbsSection.ECONOMICS)
    }

    fun fetchNews(
        section: SbsSection,
    ) {
        fetchNews { newsUseCase.getSbsNews(section).first() }
    }

    fun fetchNewsSearchResults(query: String) {
        fetchNews { newsUseCase.getGoogleNews(query).first() }
    }

    fun bookmark(newsItem: NewsModel) {
    }

    private fun fetchNews(fetcher: suspend () -> List<NewsModel>) {
        viewModelScope.launch {
            processingJob?.cancelAndJoin()
            processingJob = launch {
                val list = fetcher()
                _newsStateFlow.value = list
                list.forEachIndexed { index, news ->
                    withContext(Dispatchers.IO) {
                        val jsoup = Jsoup.connect(news.link)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .get()
                        val elements = jsoup.select("meta[property^=og:]")
                        val ogImageNode = elements.find { node ->
                            node.attr("property") == "og:image"
                        }
                        news.imageUrl = ogImageNode?.attr("content")

                        _newsImageLoadingFlow.emit(index)
                    }
                }
            }
        }
    }
}
