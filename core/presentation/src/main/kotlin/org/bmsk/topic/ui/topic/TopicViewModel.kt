package org.bmsk.topic.ui.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.bmsk.domain.repository.NewsRepository
import org.bmsk.domain.usecase.NewsUseCase
import org.bmsk.model.NewsModel
import org.bmsk.model.section.SbsSection
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase
) : ViewModel() {
    private val _newsStateFlow = MutableStateFlow<List<NewsModel>>(emptyList())
    val newsStateFlow = _newsStateFlow.asStateFlow()

    init {
        fetchNews(SbsSection.ECONOMICS)
    }

    fun fetchNews(
        section: SbsSection
    ) {
        fetchNews { newsUseCase.getSbsNews(section).first() }
    }

    fun fetchNewsSearchResults(query: String) {
        fetchNews { newsUseCase.getGoogleNews(query).first() }
    }

    fun bookmark(newsItem: NewsModel) {

    }

    suspend fun fetchOpenGraphImage() = flow {
        val newsList = _newsStateFlow.value
        newsList.forEachIndexed { index, news ->
            val jsoup = Jsoup.connect(news.link)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get()
            val elements = jsoup.select("meta[property^=og:]")
            val ogImageNode = elements.find { node ->
                node.attr("property") == "og:image"
            }
            news.imageUrl = ogImageNode?.attr("content")

            emit(index)
        }
    }.flowOn(Dispatchers.IO)

    private fun fetchNews(fetcher: suspend () -> List<NewsModel>) {
        viewModelScope.launch {
            val list = fetcher()
            _newsStateFlow.value = list
        }
    }
}